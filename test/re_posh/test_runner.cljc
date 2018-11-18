(ns re-posh.test-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [re-posh.core-test]))

(doo-tests 're-posh.core-test)
