(ns re-posh.core-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [clojure.spec.alpha :as s]
            [clojure.test.check.generators]
            [clojure.spec.test.alpha :as stest]

            [re-posh.core]
            [re-posh.db]
            [re-posh.effects]
            [re-posh.coeffects]
            [re-posh.events]
            [re-posh.subs]))

(deftest test-spec
  (testing "All functions should work as prescribed in their spec"
    (is (let [report (stest/check)
              {:keys [total check-passed]} (stest/summarize-results report)]
          (= total check-passed)))))
