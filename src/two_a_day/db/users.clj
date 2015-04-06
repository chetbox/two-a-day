(ns two-a-day.db.users
  (:require [taoensso.faraday :as far]))

(def table :two-a-day-users)

(defn users-api
  [connection-info]

  (print "Setting up users database table... ")

    (far/ensure-table connection-info
                      table
                      [:user_id :s] ; Twitter user_id
                      {:throughput {:read 1
                                    :write 1}
                       :block? true})

  (println "Done.")

  (fn [action & args]
    (println "---" action args)
    (case action
      :query           (apply far/query connection-info table args)
      :describe-table  (apply far/describe-table connection-info table args)
      :update-item     (apply far/update-item connection-info table args))))

(defn new-user-id [] (str (java.util.UUID/randomUUID)))
