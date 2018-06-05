(ns re-posh.subs
  (:require
   [re-frame.core :as r]
   [re-posh.db :refer [store]]
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
  [sub-name config-fn]
  (r/reg-sub-raw
   sub-name
   (fn [_ params]
     (let [config (config-fn @@store params)]
       (execute-sub config)))))

(defn reg-query-sub [sub-name query]
  (reg-sub
   sub-name
   (fn [_ [_ & params]]
     {:type      :query
      :query     query
      :variables params})))

(defn reg-pull-sub [sub-name pattern]
  (reg-sub
   sub-name
   (fn [_ [_ id]]
     {:type    :pull
      :pattern pattern
      :id      id})))
