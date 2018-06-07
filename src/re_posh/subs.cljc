(ns re-posh.subs
  (:require
   [re-frame.core :as r]
   [re-frame.loggers :refer [console]]
   [re-posh.db :refer [store]]
   [reagent.ratom :refer-macros [reaction]]
   [posh.reagent  :as p]))

(defmulti execute-sub :type)

(defmethod execute-sub :query
  [{:keys [query variables]}]
  (let [pre-q (partial p/q query @store)]
    (apply pre-q (into [] variables))))

(defmethod execute-sub :pull
  [{:keys [pattern id]}]
  (p/pull @store pattern id))

(defn reg-sub
  "For a given `query-id` register a `config` function and input `signals`

  At an abstract level, a call to this function allows you to register 'the mechanism'
  to later fulfil a call to `(subscribe [query-id ...])`.

  To say that another way, reg-sub allows you to create a template for a node
  in the signal graph. But note: reg-sub does not cause a node to be created.
  It simply allows you to register the template from which such a
  node could be created, if it were needed, sometime later, when the call
  to `subscribe` is made.

  reg-sub needs three things:
    - a `query-id`
    - the required inputs for this node
    - a function that generates config for query or pull for this node

  The `query-id` is always the 1st argument to reg-sub and it is typically
  a namespaced keyword.

  A config function is always the last argument and it has this general form:
  `(input-signals, query-vector) -> a-value`

  What goes in between the 1st and last args can vary, but whatever is there will
  define the input signals part of the template, and, as a result, it will control
  what values the config functions gets as a first argument.
  There's 3 ways this function can be called - 3 ways to supply input signals:

  1. No input signals given:

     (reg-sub
       :query-id
       a-config-fn)   ;; (fn [db v]  ... a-value)
     The node's input signal defaults to datascript database, and the value within `ds` is
     is given as the 1st argument to the computation function.

  2. A signal function is supplied:

     (reg-sub
       :query-id
       signal-fn     ;; <-- here, the form is (fn [db v] ... signal | [signal])
       config-fn)

     When a node is created from the template, the `signal-fn` will be called and it
     is expected to return the input signal(s) as either a singleton, if there is only
     one, or a sequence if there are many.
     The values from the nominated signals will be supplied as the 1st argument to the
     config function - either a singleton or sequence, paralleling
     the structure returned by the signal function.
     Here, is an example signal-fn, which returns a vector of input signals.
       (fn [query-vec]
         [(subscribe [:a-sub])
          (subscribe [:b-sub])])
     For that signal function, the config function must be written
     to expect a vector of values for its first argument.
       (fn [[a b] _] ....)
     If the signal function was simpler and returned a singleton, like this:
        (fn [query-vec dynamic-vec]
          (subscribe [:a-sub]))
     then the config function must be written to expect a single value
     as the 1st argument:
        (fn [a _] ...)

  3. Syntax Sugar

     (reg-sub
       :a-b-sub
       :<- [:a-sub]
       :<- [:b-sub]
       (fn [[a b] [_]] {:a a :b b}))

  This 3rd variation is syntactic sugar for the 2nd. Pairs are supplied instead
  of an `input signals` functions. Each pair starts with a `:<-` and a subscription
  vector follows.
  "
  [query-id & args]
  (let [config-fn  (last args)
        input-args (butlast args)
        err-header (str "re-posh: reg-sub for " query-id ", ")
        inputs-fn  (case (count input-args)
                     ;; no `inputs` function provided - give the default
                     0 (fn
                         ([_] nil)
                         ([_ _] nil))

                     ;; a single `inputs` fn
                     1 (let [f (first input-args)]
                         (when-not (fn? f)
                           (console :error err-header "2nd argument expected to ba an inputs function, got: " f))
                         f)

                     ;; one sugar pair
                     2 (let [[marker vec] input-args]
                         (when-not (= :<- marker)
                           (console :error err-header "expected :<-, got: " marker))
                         (fn inp-fn
                           ([_] (r/subscribe vec))
                           ([_ _] (r/subscribe vec))))

                     ;; multiple sugar pairs
                     (let [pairs (partition 2 input-args)
                           markers (map first pairs)
                           vecs (map last pairs)]
                       (when-not (and (every? #{:<-} markers) (every? vector? vecs))
                         (console :error err-header "expected pairs of :<- and vectors, got:" pairs))
                       (fn inp-fn
                         ([_] (map r/subscribe vecs))
                         ([_ _] (map r/subscribe vecs)))))]
    (r/reg-sub-raw
     query-id
     (fn [_ params]
       (if (= (count input-args) 0)
         ;; if there is no inputs-fn provided (or sugar version) don't wrap anything in reaction,
         ;; just return posh's query or pull
         (execute-sub (config-fn @@store params))
         (reaction
          (let [inputs (inputs-fn params)
                signals (if (list? inputs)
                          (map deref inputs)
                          (deref inputs))]
            @(execute-sub (config-fn signals params)))))))))

(defn reg-query-sub
  "Syntax sugar for writing queries. It allows writing query subscription
   in a very simple way:

  (re-posh/reg-query-sub
   :query-id
   '[:find ...
     :in $ $1 $2  ;; <- all variables go here
     :where ...])

  It's possible to subscibe to this query with

  (re-posh/subscribe [:query-id var-1 var-2])

  so that variables `var-1` and `var-2` will be automatically sent to `:in` form
  "
  [sub-name query]
  (reg-sub
   sub-name
   (fn [_ [_ & params]]
     {:type      :query
      :query     query
      :variables params})))

(defn reg-pull-sub
  "Syntax sugar for writing pull queries. It allows writing pull subscription
  in a very simple way:

  (re-posh/reg-pull-sub
   :pull-id
   '[*]) ;;<- pull pattern

  It's possible to subscribe to this pull query with

  (re-posh/subscibe [:pull-id id])

  Where id is an entity id"
  [sub-name pattern]
  (reg-sub
   sub-name
   (fn [_ [_ id]]
     {:type    :pull
      :pattern pattern
      :id      id})))
