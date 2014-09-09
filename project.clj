(defproject nac "0.1.0-SNAPSHOT"
  :description "Naughts and Crosses in the Om"
  :url "http://neganp.github.io/projects/nac"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2311"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [org.clojure/math.combinatorics "0.0.8"]
                 [om "0.7.1"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]]

  :source-paths ["src"]

  :cljsbuild { 
    :builds [{:id "nac"
              :source-paths ["src"]
              :compiler {
                :output-to "nac.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}]})
