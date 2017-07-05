
(ns app.comp.page-files
  (:require-macros [respo.macros :refer [defcomp <> span div pre input button a]])
  (:require [clojure.string :as string]
            [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo-ui.style.colors :as colors]
            [respo.core :refer [create-comp]]
            [respo.comp.space :refer [=<]]
            [app.style :as style]))

(defn on-add [state]
  (fn [e d! m!]
    (let [text (string/trim (:ns-text state))]
      (if (not (string/blank? text)) (do (d! :ir/add-ns text) (m! (assoc state :ns-text "")))))))

(defn on-input-def [state] (fn [e d! m!] (m! (assoc state :def-text (:value e)))))

(def style-def {:cursor :pointer})

(defn on-add-def [state]
  (fn [e d! m!]
    (let [text (string/trim (:def-text state))]
      (if (not (string/blank? text))
        (do (d! :ir/add-def text) (m! (assoc state :def-text "")))))))

(defn on-edit-procs [e d! m!] (d! :session/edit-procs nil))

(def style-link {:cursor :pointer})

(defn on-edit-ns [e d! m!] (d! :session/edit-ns nil))

(defn on-edit-def [text] (fn [e d! m!] (d! :session/edit-def text)))

(defn render-file [state selected-ns defs-set]
  (div
   {:style ui/flex}
   (div {} (<> span "File" style/title) (=< 16 nil) (<> span selected-ns nil))
   (div
    {}
    (span {:inner-text "ns", :style style-link, :on {:click on-edit-ns}})
    (=< 16 nil)
    (span {:inner-text "procs", :style style-link, :on {:click on-edit-procs}}))
   (div
    {}
    (->> defs-set
         (map
          (fn [def-text]
            [def-text
             (div
              {:style style-def, :on {:click (on-edit-def def-text)}}
              (<> span def-text nil))]))))
   (div
    {}
    (input
     {:value (:def-text state),
      :placeholder "a def",
      :style style/input,
      :on {:input (on-input-def state)}})
    (=< 8 nil)
    (button {:inner-text "Add def", :style style/button, :on {:click (on-add-def state)}}))))

(def style-ns {:cursor :pointer})

(def style-list {:width 320})

(defn on-checkout [state ns-text] (fn [e d! m!] (d! :session/select-ns ns-text)))

(defn on-input-ns [state] (fn [e d! m!] (m! (assoc state :ns-text (:value e)))))

(defn render-empty [] (div {} (<> span "Empty" nil)))

(def initial-state {:ns-text "", :def-text ""})

(defn render-list [ns-set state]
  (div
   {:style style-list}
   (div {:style style/title} (<> span "Namespaces" nil))
   (div
    {}
    (->> ns-set
         (map
          (fn [ns-text]
            [ns-text
             (div
              {:style style-ns, :on {:click (on-checkout state ns-text)}}
              (<> span ns-text nil))]))))
   (div
    {}
    (input
     {:value (:ns-text state),
      :placeholder "a namespace",
      :style style/input,
      :on {:input (on-input-ns state)}})
    (=< 8 nil)
    (button {:inner-text "Add ns", :style style/button, :on {:click (on-add state)}}))))

(defcomp
 comp-page-files
 (states selected-ns router-data)
 (let [state (or (:data states) initial-state)]
   (div
    {:style (merge ui/flex ui/row)}
    (render-list (:ns-set router-data) state)
    (if (some? selected-ns)
      (render-file state selected-ns (:defs-set router-data))
      (render-empty)))))
