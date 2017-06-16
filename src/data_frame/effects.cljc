(ns re-posh.effects
  (:require
    [re-frame.core :as r]
    [re-posh.db :refer [store]]
    [posh.reagent  :as p]))

(r/reg-fx
  :transact
  (fn [datoms]
    (p/transact! @store datoms)))
