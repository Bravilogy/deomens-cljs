(ns admin.components.markdown-editor.view
  (:require ["simplemde" :as SimpleMde]
            [reagent.core :as r]))

(defn render
  "helper component that will attach MDE to input"
  [props]
  (let [mde      (r/atom {})
        handlers (->> (:events props)
                      (map (fn [[k f]]
                             [k #(f (.value @mde))]))
                      (into {}))]
    (r/create-class
     {:component-did-mount
      (fn [self]
        (reset! mde (SimpleMde.
                     (clj->js {:element      (r/dom-node self)
                               :initialValue (:value props)})))
        (doseq [[ev handler] handlers]
          (.on (.-codemirror @mde) (name ev) handler)))

      :component-will-unmount
      (fn []
        (doseq [[ev handler] handlers]
          (.off (.-codemirror @mde) (name ev) handler)))

      :reagent-render
      (fn [] [:textarea.form-control])})))
