(ns re-posh.events
  (:require [re-frame.core :as r]))

(defn reg-event-ds
  ([event-name interceptors handler]
   (r/reg-event-fx
     event-name
     (into [] (concat [(r/inject-cofx :ds)] interceptors))
     (fn [{:keys [ds]} signal]
       { :transact (handler ds signal) })))
  ([event-name handler]
   (reg-event-ds event-name [] handler)))
