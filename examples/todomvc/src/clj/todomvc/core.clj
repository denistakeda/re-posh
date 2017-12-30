(ns todomvc.core
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [resource-response]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))



;; Extra Data
(def extra-entries
  [{ :db/id            -1
    :app/type         :type/task
    :task/title       "This ToDo was taken from the Server"
    :task/description "Server"
    :task/done?       false}
   { :db/id            -2
    :app/type         :type/task
    :task/title       "This ToDo was ALSO taken from the Server"
    :task/description "Server"
    :task/done?       false}])


;; Handlers
(defroutes routes
           (GET "/" [] (resource-response "index.html" {:root "public"}))
           (GET "/upload" []
             {:status 200
              :headers {"Content-Type" "application/edn; charset=utf-8"}
              :body (pr-str extra-entries)})
           (resources "/"))

(def dev-handler (-> #'routes wrap-reload))


;; Main
(defn -main [& args]
  (run-jetty routes {:port 5000 :join? false}))
