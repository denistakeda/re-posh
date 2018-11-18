(defproject re-posh "0.3.0"
  :description "Use your re-frame with DataScript as a data storage"
  :url "https://github.com/denistakeda/re-posh"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.238"]
                 [datascript "0.16.6"]
                 [re-frame "0.10.5"]
                 [posh "0.5.5"]]
  :plugins [[lein-doo "0.1.10"]]
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.9.0"]]}}
  :cljsbuild
  {:builds [{:id "test"
             :source-paths ["src" "test"]
             :compiler {:output-to "resources/public/js/testable.js"
                        :output-dir "resources/public/js"
                        :main re-posh.test-runner
                        :optimizations :none}}]})
