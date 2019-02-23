(ns ribelo.cljs-await.core
  (:require [cljs.core.async :as a]))


(defmacro async [& body]
  `(let [out# (a/chan)]
     (a/go
       (if-let [res# (do ~@body)]
         (a/>! out# res#)
         (a/close! out#)))
     out#))


(defmacro await [bindings & body]
  {:pre [(even? (count bindings))]}
  `(let [~@(mapcat (fn [[bind# val#]]
                     (list bind# (list 'a/<! val#)))
                   (partition 2 bindings))]
     ~@body))
