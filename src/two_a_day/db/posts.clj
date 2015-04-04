(ns two-a-day.db.posts
  (:require [taoensso.faraday :as far]))

(def table :two-a-day-posts)

(defn posts-api
  [connection-info]

  (print "Setting up posts database table... ")

  (far/ensure-table connection-info
                    table
                    [:user-id :s]
                    {:range-keydef [:date :s]
                     :throughput {:read 1
                                  :write 1}
                     :block? true})

  (println "Done.")

  (fn [action & args]
    (case action
      :query       (apply far/query connection-info table args)
      :update-item (apply far/update-item connection-info table args))))
