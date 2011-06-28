(ns clj-oauth2.uri
  (:use [clojure.contrib.java-utils]
        [clojure.pprint :only (write)])
  (:require [clojure.string :as str]
            [clj-http.client :only parse-url :as http])
  (:import [java.net URI URLEncoder URLDecoder]
           [javax.ws.rs.core UriBuilder]
           [java.lang IllegalArgumentException]))

;; taken from https://github.com/marktriggs/clojure-http-client/blob/master/src/clojure/http/client.clj
;; modified to use ASCII instead of UTF-8
(defn url-encode
  "Wrapper around java.net.URLEncoder returning an ASCII URL encoded
 representation of argument, either a string or map."
  [arg]
  (if (map? arg)
    (str/join \& (map (fn [[k v]]
                        (str (url-encode (name k))
                             \=
                             (url-encode v)))
                      arg))
    (URLEncoder/encode (str arg) "UTF-8")))

(defn url-decode [str]
  (URLDecoder/decode str "UTF-8"))

(defn form-url-decode [str]
  (into {}
        (map (fn [p] (vector (keyword (first p)) (second p)))
             (map (fn [s] (map url-decode (str/split s #"=")))
                  (str/split str #"&")))))

(defmacro uri-as-map [uri]
  `(hash-map
    ~@(mapcat
       (fn [[key getter]]
         `(~key (. ~uri ~getter)))
       '((:scheme getScheme)
         (:user-info getUserInfo)
         (:host getHost)
         (:port getPort)
         (:path getPath)
         (:query getQuery)
         (:fragment getFragment)))))

(defn parse-uri
  ([uri] (parse-uri uri true))
  ([uri form-url-decode-query?]
     (let [uri (uri-as-map (URI. uri))]
       (if (and form-url-decode-query? (:query uri))
         (assoc uri :query (form-url-decode (:query uri)))
         uri))))

(defn make-uri [arg]
  (let [uri (.. (UriBuilder/fromUri "")
                (scheme (:scheme arg))
                (userInfo (:user-info arg))
                (host (:host arg))
                (port (or (:port arg) -1))
                (path (or (:path arg) ""))
                (fragment (:fragment arg)))
        uri (cond (string? (:query arg))
                  (. uri replaceQuery (:query arg))
                  (map? (:query arg))
                  (reduce (fn [u [k v]]
                            (. uri queryParam
                               (name k)
                               (to-array (if (vector? v) v [v]))))
                          uri
                          (:query arg))
                  :else uri)]
    (. uri build (to-array []))))