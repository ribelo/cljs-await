(ns ribelo.cljs-await.core
  (:require [cljs.core.async :refer [chan go >! <! close! put!]]))


(defmacro async [& body]
  `(let [out# (chan)]
     (go
       (if-let [res# (do ~@body)]
         (>! out# res#)
         (close! out#)))
     out#))


(defmacro await [bindings & body]
  {:pre [(even? (count bindings))]}
  `(let [~@(mapcat (fn [[bind# val#]]
                     (list bind# (list '<! val#)))
                   (partition 2 bindings))]
     ~@body))


(defmacro <p [promise & {:keys [on-failure]}]
  (let [c (chan)]
    (-> promise
        (.then #(put! c %))
        (cond->
          on-failure (.catch (fn [] (put! c false) (on-failure)))))))
