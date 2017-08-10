(ns re-posh.coeffects
  (:require
    [re-frame.core :as r]
    [re-posh.db :refer [store]]
    [posh.reagent  :as p]))

(r/reg-cofx
  :ds
  (fn [coeffects _]
    (assoc coeffects :ds @@store)))
