(ns chatbot.core
  (:require [chatbot.identify_keyword :refer [keyword-response-main]]
            [chatbot.parse :refer [parse-input]]
            [chatbot.bot_utils :as bot]
            [chatbot.park_utils :as park]
            [chatbot.user_utils :as chat-user]
            [web.backend :as web]))

(defn main-loop
  "When called without arguments, a REPL chatbot is started.
  It consumes user input until a terminating keyword is met.
  The main loop calls help function if user input is help.
  Checks if the keyword is not identified and prints the random error message.
  Otherwise greets user or answers the questions about the park."

  [& args]
  (when (some #(= "--web" %) args)
    (web/run-backend! args))
  (bot/bot-print! "Hi!")
  (bot/bot-print! "I am your park guide.")
  (chat-user/set-user-prompt!)
  (bot/bot-print! (str "You can change your username at any time "
                       "by typing 'username'."))
  (park/user-select-park!)
  (bot/bot-print! "To end the conversation, enter 'finish'.")
  (bot/bot-print! "If you need help, type 'help'.")
  (bot/bot-print! "History of the park can be viewed by entering 'history'.")
  (bot/bot-print! "If you want to change the park type 'park'.")
  (loop [user-input (parse-input (chat-user/get-user-input))]
    (if (bot/finish? user-input)
      (bot/bot-print! (rand-nth bot/possible-goodbye-messages))
      (let [help? (= '("help") user-input)
            username-change? (= '("username") user-input)
            park-history? (= '("history") user-input)
            greeting? (bot/greeting bot/possible-greetings user-input)
            park-change? (= '("park") user-input)
            response (keyword-response-main user-input)]
        (cond
          help?
          (bot/help-function)

          username-change?
          (chat-user/set-user-prompt!)

          park-history?
          (bot/bot-print! (park/park-history))

          greeting?
          (bot/bot-print! (bot/greeting bot/possible-greetings user-input))
          park-change?
          (park/user-select-park!)

          response
          (bot/bot-print! (park/find-park-data response))

          :else (bot/bot-print! (rand-nth bot/possible-error-messages)))

        (recur (parse-input (chat-user/get-user-input)))))))
