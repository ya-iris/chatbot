(ns chatbot.core
  (:require [chatbot.get_data :refer [create-data]]
            [clojure.string :as str]
            [cheshire.core :refer [parse-string]]))

(defn parse-input [input]
  (let [words (str/split input #" ")
        lower-cased-words (map str/lower-case words)]
    lower-cased-words))

(defn parse-json [file-name]
  (let [file (slurp file-name)]
    (parse-string
      file
      (fn [k]
        (keyword
          (str/lower-case
            (clojure.string/join "-"
                                 (clojure.string/split k #" "))))))))

(defn main
  ""
  []
  (create-data))
