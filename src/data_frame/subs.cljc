(ns data-frame.subs
  (:require
    [re-frame.core :as r]
    [data-frame.db :refer [store]]
    [posh.reagent  :as p]))

(defn reg-query-sub [sub-name query]
  (r/reg-sub-raw
    sub-name
    (fn [_ [_ & params]]
      (let [pre-q (partial p/q query @store)]
        (apply pre-q params)))))

(defn reg-pull-sub [sub-name pattern]
  (r/reg-sub-raw
    sub-name
    (fn [_ [_ id]]
      (p/pull @store pattern id))))
