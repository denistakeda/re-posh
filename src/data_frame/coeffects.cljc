(ns data-frame.coeffects
  (:require
    [re-frame.core :as r]
    [data-frame.db :refer [store]]
    [posh.reagent  :as p]))

(r/reg-cofx
  :ds
  (fn [coeffects _]
    (assoc coeffects :ds @store)))
