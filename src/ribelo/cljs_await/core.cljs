(ns ribelo.cljs-await.core
  (:require-macros [ribelo.cljs-await.core])
  (:require [cljs.core.async :as a]))


(defn <p [promise & {:keys [on-failure]}]
      (let [c (a/chan)]
           (-> promise
               (.then #(a/put! c %))
               (cond->
                 on-failure (.catch (fn [] (a/put! c false) (on-failure)))))))
