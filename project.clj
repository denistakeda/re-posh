(defproject re-posh "0.3.1-SNAPSHOT"
  :description "Use your re-frame with DataScript as a data storage"
  :url "https://github.com/denistakeda/re-posh"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [datascript "0.18.7"]
                 [re-frame "0.10.7"]
                 ;; NOTE: @denistakeda using my private fork, but now that you have Posh ownership
                 ;;       release Posh with pull-many support & update below dependency
                 [org.clojars.questyarbrough/posh "0.5.8-SNAPSHOT"]])
