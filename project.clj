(def dev-dependencies
  '[[ring "1.1.5"]])

(defproject social.forks/clj-oauth2 "0.5.3-socialsuperstore-SNAPSHOT"
  :description "clj-http and ring middlewares for OAuth 2.0"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [cheshire "5.0.2"]
                 [clj-http "0.5.6"]
                 [uri "1.1.0"]
                 [commons-codec/commons-codec "1.6"]]
  :exclusions   [org.clojure/clojure-contrib]
  :dev-dependencies ~dev-dependencies
  :profiles {:dev {:dependencies ~dev-dependencies}}

  :repositories {"stuartsierra-releases" "http://stuartsierra.com/maven2"
                 "releases" {:url "https://larder.socialsuperstore.com"
                             :creds :gpg}}

  :aot [clj-oauth2.OAuth2Exception
        clj-oauth2.OAuth2StateMismatchException]

  :aliases {"release" ["deploy" "releases"]})
