(ns random-person
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [clojure.string :as str]))

(defn split-str-to-col [s] (str/split s #","))

(defn find-random [request]
  (let [names (split-str-to-col (get-in request [:query-params :names]))]
    {:status 200 :body (rand-nth names)}))

(def routes
  (route/expand-routes
   #{
     ["/random" :get find-random :route-name :find-random]
     }))

(def service-map
  {::http/routes routes
   ::http/type   :jetty
   ::http/port   8890
   ::http/host "0.0.0.0" ;; https://github.com/pedestal/pedestal/issues/604
   })

(defn start []
  (http/start (http/create-server service-map)))

(defonce server (atom nil))

(defn start-dev []
  (reset! server
          (http/start (http/create-server
                       (assoc service-map
                              ::http/join? false)))))

(defn stop-dev []
  (http/stop @server))

(defn restart []
  (stop-dev)
  (start-dev))

(defn run [opts]
  (start))
