(ns routes.test.helper
  (:use clojure.test
        routes.helper))

(deftest link-to-test
  (is (= (link-to "http://example.com/")
         [:a {:href "http://example.com/"} nil]))
  (is (= (link-to "http://example.com/" "foo")
         [:a {:href "http://example.com/"} (list "foo")]))
  (is (= (link-to "http://example.com/" "foo" "bar")
         [:a {:href "http://example.com/"} (list "foo" "bar")])))

(deftest test-routes
  (is (nil? (route :unknown-route))))

(deftest test-register
  (let [example-route {:name :example-route}]
    (register example-route)
    (is (= example-route (route (:name example-route))))))

(deftest test-format-pattern
  (are [pattern args expected]
    (is (= expected (apply format-pattern pattern args)))
    "/continents/:iso-3166-1-alpha-2-:name"
    [{:iso-3166-1-alpha-2 "eu" :name "Europe"}]
    "/continents/eu-europe"
    "/:a/:b-:c/d/:e-f-g"
    [{:a 1} {:b 2 :c 3} {:e-f-g 4}]
    "/1/2-3/d/4"))

(deftest test-identifier
  (are [test expected]
    (is (= expected test))
    (identifier
     {:iso-3166-1-alpha-2 "eu"}
     [:iso-3166-1-alpha-2 :name])
    "eu"
    (identifier
     {:iso-3166-1-alpha-2 "eu"
      :name "Europe"}
     [:iso-3166-1-alpha-2 :name])
    "eu-europe"
    (identifier
     {:iso-3166-1-alpha-2 "na"
      :name "North America"}
     [:iso-3166-1-alpha-2 :name])
    "na-north-america"
    (identifier
     {:id 1 :name "Northern California"}
     [:id :name])
    "1-northern-california"))

(deftest test-path
  (are [segments expected]
    (is (= expected (apply path segments)))
    ["continents"]
    "/continents"
    ["/continents"]
    "/continents"
    ["//continents"]
    "/continents"
    ["continents" "eu-europe"]
    "/continents/eu-europe"
    ["/continents" "/eu-europe"]
    "/continents/eu-europe"))

(deftest test-parse-keys
  (are [pattern expected]
    (is (= expected (parse-keys pattern)))
    "/continents/:iso-3166-1-alpha-2-:name"
    [[:iso-3166-1-alpha-2 :name]]
    "/continents/:iso-3166-1-alpha-2-:name"
    [[:iso-3166-1-alpha-2 :name]]
    "/:a/:b-:c/d/:e-f-g"
    [[:a] [:b :c] [:e-f-g]]))

(deftest test-parse-url
  (are [url expected]
    (is (= expected (parse-url url)))
    "api.burningswell.com"
    {:scheme :https :server-name "api.burningswell.com" :server-port nil :uri nil}
    "api.burningswell.com:80"
    {:scheme :https :server-name "api.burningswell.com" :server-port 80 :uri nil}
    "http://api.burningswell.com:81/base"
    {:scheme :http :server-name "api.burningswell.com" :server-port 81 :uri "/base"}))
