(ns ribelo.cljs-await.core
  (:require [cljs.core.async :as a]))


(defmacro async [& body]
  `(let [out# (a/promise-chan)]
     (a/go
       (if-let [res# (do ~@body)]
         (a/put! out# res#)
         (a/close! out#)))
     out#))


(defmacro await [bindings & body]
  {:pre [(even? (count bindings))]}
  `(let [~@(mapcat (fn [[bind# val#]]
                     (list bind# (list 'a/<! val#)))
                   (partition 2 bindings))]
     ~@body))


(defmacro <p [promise & {:keys [on-failure]}]
  `(let [out# (a/promise-chan)]
     (-> ~promise
         (.then #(a/put! out# (or % false)))
         (cond->
           ~on-failure (.catch (fn [] (a/put! out# false) (do ~@on-failure)))))
     (a/<! out#)))
