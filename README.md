[![Clojars Project](https://img.shields.io/clojars/v/re-posh.svg)](https://clojars.org/re-posh)
[![Gitter](https://img.shields.io/gitter/room/thunder-project/thunder.svg?style=flat-square)](https://gitter.im/re-posh/Lobby?utm_source=share-link&utm_medium=link&utm_campaign=share-link)

# re-posh

`re-posh` is a ClojureScript library that empowers you to improve your [re-frame](https://github.com/Day8/re-frame) single-page applications to have a robust and flexible in-page database to manage your application's state.

You can also leverage [DatSync](https://github.com/metasoarous/datsync) to have that in-browser app sync to a server running [Datomic](http://www.datomic.com/) or [DataScript](https://github.com/tonsky/datascript) to simply store your data, or for realtime collaboration between multiple clients.

re-frame is a [reactive programming](https://gist.github.com/staltz/868e7e9bc2a7b8c1f754) library for writing single-page apps in ClojureScript
 using [Reagent](https://github.com/reagent-project/reagent), which wraps Facebook's popular [React](https://facebook.github.io/react/) library for building component-oriented user interfaces.

[Posh](https://github.com/denistakeda/posh) improves Reagent to allow the declarative binding of user interface components to a local DataScript database. Like Datomic, DataScript supports the powerful `q` and `pull` query API's, and [Datalog](http://www.learndatalogtoday.org/) in general.

`re-posh` allows Posh and re-frame to work together by adding support for re-frame specific `subscriptions`, `events`, `effects`, and `co-effects` to Posh.

## Why?

State management within *any* application, if treated as a secondary concern, can become a creeping problem that becomes a source of difficult-to-debug problems as the app grows in complexity. re-frame offers a solution to that problem by [having a single app-db data structure](https://github.com/Day8/re-frame/blob/master/docs/ApplicationState.md) in the form of a `reagent/atom` as a single source of truth in the app. UI elements subscribe to changes to that structure and create events that mutate that structure.

 `re-posh` replaces that single `atom` with a full-featured in-memory database. Now, your application's state has sophisticated data management and querying capabilities that defy complexity. UI elements can bind to the db with all of the expressiveness of Datalog, and the app maintains a single source of truth with an *actual* database.

## Usage

Start a re-frame project and include this dependency:

[![Clojars Project](https://clojars.org/re-posh/latest-version.svg)](https://clojars.org/re-posh)


Require `re-posh` in your app:
```clojure
(ns example
  (:require
    [re-posh.core :refer [reg-query-sub reg-pull-sub reg-event-ds subscribe dispatch]]))
```

## Connection

Connect your DataScript database to `re-posh`:
```clojure
(ns example.db
  (:require
    [datascript.core    :as d]
    [re-posh.core       :refer [connect!]]))

(def conn (d/create-conn))
(connect! conn)
```

## Subscriptions

You can subscribe to the DataScript database with a query subscription or with a pull subscription.

### Basic subscription

The basic mechanism for subscibing to a DataScript database is `reg-sub` function. It uses the same mechanism and API as [re-frame's subscribe](https://github.com/Day8/re-frame/blob/master/src/re_frame/subs.cljc#L200-L354). You can find the working example at [todomvc](https://github.com/denistakeda/re-posh/blob/master/examples/todomvc/src/cljs/todomvc/subs.cljs) project.

For a given `query-id` function `reg-sub` register a `config` function and input `signals`.

There are 2 forms of configs that can be returned by config function:

For query:
```clojure
{:type      :query
 :query     '[:find ... where ...]
 :variables [var1 var2]}
```
Variables are optional. Please visit the [datalog query](https://docs.datomic.com/on-prem/query.html) documentation to learn about query syntax.

For pull subscibtion:
```clojure
{:type    :pull
 :pattern '[*]
 :id      id}
```
All fields are required. Please visit the [datomic pull](https://docs.datomic.com/on-prem/pull.html) documentation to learn about pull syntax and patterns.

At an abstract level, a call to `reg-sub` function allows you to register 'the mechanism' to later fulfil a call to `(subscribe [query-id ...])`.

To say that another way, reg-sub allows you to create a template for a node in the signal graph. But note: reg-sub does not cause a node to be created. It simply allows you to register the template from which such a node could be created, if it were needed, sometime later, when the call to `subscribe` is made.

reg-sub needs three things:
 - a `query-id`
 - the required inputs for this node
 - a function that generates config for query or pull for this node

The `query-id` is always the 1st argument to reg-sub and it is typically a namespaced keyword.

A config function is always the last argument and it has this general form: `(input-signals, query-vector) -> a-value`

What goes in between the 1st and last args can vary, but whatever is there will define the input signals part of the template, and, as a result, it will control what values the config functions gets as a first argument. There's 3 ways this function can be called - 3 ways to supply input signals:

1. No input signals given:

```clojure
(reg-sub
 :query-id
 a-config-fn)   ;; (fn [db v]  ... a-value)
```

The node's input signal defaults to datascript database, and the value within `ds` is given as the 1st argument to the computation function.

2. A signal function is supplied:

```clojure
(reg-sub
 :query-id
 signal-fn     ;; <-- here, the form is (fn [db v] ... signal | [signal])
 config-fn)
```

When a node is created from the template, the `signal-fn` will be called and it is expected to return the input signal(s) as either a singleton, if there is only one, or a sequence if there are many. The values from the nominated signals will be supplied as the 1st argument to the config function - either a singleton or sequence, paralleling the structure returned by the signal function.
Here, is an example signal-fn, which returns a vector of input signals.

```clojure
(fn [query-vec]
 [(subscribe [:a-sub])
  (subscribe [:b-sub])])
```

For that signal function, the config function must be written to expect a vector of values for its first argument.

```clojure
(fn [[a b] _] ....)
```

If the signal function was simpler and returned a singleton, like this:

```clojure
(fn [query-vec dynamic-vec]
  (subscribe [:a-sub]))
```

then the config function must be written to expect a single value as the 1st argument:
```clojure
(fn [a _] ...)
```

3. Syntax Sugar

```clojure
(reg-sub
  :a-b-sub
  :<- [:a-sub]
  :<- [:b-sub]
  (fn [[a b] [_]] {:a a :b b}))
```

This 3rd variation is syntactic sugar for the 2nd. Pairs are supplied instead of an `input signals` functions. Each pair starts with a `:<-` and a subscription


### Query subscription

You can use `reg-query-sub` function for subscribe to any query

```clojure
(reg-query-sub
  :task-ids
  '[ :find  [?tid ...]
     :where [?tid :task/title]])
```

This function takes two params, subscription name and datalog [query](http://docs.datomic.com/query.html#sec-5). You can use this subscription as regular re-frame subscription

```clojure
(defn page []
  (let [task-ids (subscribe [:task-ids])]
    (fn []
       ...
```

Every parameter in a signal will be pass as param to the query

```clojure
(reg-query-sub
   :task-ids
   '[ :find  [?tid ...]
      :in $ ?param-1 ?param-2
      :where ... ]

(let [task-ids (subscribe [:task-ids param-1 param-2])]
   ...)
```

### Pull subscription

Pull subscriptions creates subscription to the entity. `reg-pull-sub` function create pull subscription and takes two params, subscription name and pull pattern. For more details see [Datomic Pull](http://docs.datomic.com/pull.html)

```clojure
(reg-pull-sub
   :sub-name
   '[*])

 ;; Usage

 (let [entity-id 123
       entity    (subscribe [:sub-name entity-id])])
```

Pull-many subscriptions are similar to pull but take a vector of entity-ids.

```clojure
(reg-pull-many-sub
   :sub-name
   '[*])

 ;; Usage

 (let [entity-id 123
       entity    (subscribe [:sub-name [eids]])])
```

### Combining subscriptions

It's often the case that combining of several subscriptions (espetially query and pull subscriptions) required. Unfortunatelly `re-posh` doesn't support combining them inside query. But there is another way. The classical example is when we have a query that returns some object id and we needs the whole object (pull). Here is how can we do that:

```clojure
(reg-sub
 :configuration-form-id
 (fn [_ _]
  {:type  :query
   :query '[:find ?id .
            :where :app/type :type/configuration-form]}))

(reg-sub
 :configuration-form
 :<- [:configuration-form-id]
 (fn [form-id _]
  {:type    :pull
   :pattern '[*]
   :id      form-id}))
```

In this example two queries are generated. The first one is independent. It returns the id of required object. The second one depends of the first one. It takes the object id as param and returns the whole object.

Another example to show usage of `pull-many` from a query that returns several entity-ids.

```clojure
(reg-sub
 :todo-ids
 (fn [_ _]
  {:type  :query
   :query '[:find ?id
            :where [?id :item/type :type/todo]]}))

(reg-sub
 :todos
 :<- [:todo-ids]
 (fn [entity-ids _]
  {:type    :pull-many
   :pattern '[*]
   :ids      (reduce into [] entity-ids)}))
```

Note the `(reduce ...)` & recall that the query returns its results in form `#{[1] [2] ...}`, but the pull-many sub expects a sequence of entity-ids.

 ## Events

`re-posh` uses totally the same solution as re-frame `reg-event-db` but with datascript database instead. Function `reg-event-ds` takes event name and event handler. First param for handler is a dereferenced DataScript database. You can do with it whatewer you like, make query or take entities with pull. The second parameter is a signal. Event handler have to return transaction.

 ```clojure
 (reg-event-ds
  :update-task
  (fn [ds [_ id path value]] ;; ds is not used here, just an example
    [[:db/add id path value]]))
 ```

 ## Effects and Co-effects

`re-posh` introduce one effect and one co-effect. You can use them as regular re-frame effects and co-effects (in fact they are regular re-frame effects and coeffects)


### Transact effect

This effect commit transaction into the DataScript database

```clojure
(ns example.events
   (:require [re-posh.core :as re-posh]))

(re-posh/reg-event-fx
   :my-event
   (fn [cofx [_ id k v]]
      {:transact [[:db/add id k v]]})) ;; return datascript transaction
```

### DS co-effect

This co-effect provide DataScript database into your event handler

```clojure
(ns example.events
   (:require [re-posh.core :as re-posh]))

(re-posh/reg-event-fx
   :my-event
   [(re-posh/inject-cofx :ds)] ;; inject coeffect
   (fn [{:keys [ds]} [_ id k v]] ;; ds here is the DataScript database
      {:transact [[:db/add id k v]]}))
```

## Examples

[todomvc](https://github.com/denistakeda/re-posh/tree/master/examples/todomvc)

## Contribution

Pull requests are welcome. Email me on <denis.takeda@gmail.com> if you have any questions, suggestions or proposals.

 ## License

 Copyright © 2020 Denis Krivosheev

 Distributed under the MIT License
