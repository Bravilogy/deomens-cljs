(ns admin.compositions.project.partials.text-node
  (:require ["moment" :as moment]
            [clojure.string :as s]
            [re-frame.core :as rf]
            [cljss.core :refer-macros [defstyles]]
            [admin.components.markdown-editor.view :as markdown-editor]))

(defstyles node-icon
  []
  {:color "green"})

(defn- create-excerpt
  [text]
  (str (->> (s/split text " ")
            (take 3)
            (interpose " ")
            (apply str))
       "..."))

(defn render
  [{on-update :on-update
    node-id   :node-id
    node-item :node-item}]
  (rf/dispatch [:reset-form :edit-node (:id node-item) node-item])
  (let [form-state (rf/subscribe [:form-state :edit-node (:id node-item)])]
    (fn [{is-collapsed?          :is-collapsed?
         controls               :controls
         {body       :body
          created-at :created_at
          :as        node-item} :node-item}]
      [:<>
       [:div.card-header
        controls
        [:div.user-pic
         [:i.fa.fa-text-width {:class (node-icon)}]]
        [:h5 (create-excerpt body)]
        [:h6
         [:div.time "Created " (-> created-at moment .fromNow)]]]
       (when (not is-collapsed?)
         [:<>
          [:div.card-description
           [markdown-editor/render
            {:value  body
             :events {:change #(rf/dispatch [:update-form-field
                                             [:edit-node (:id node-item)]
                                             :body
                                             %])
                      :blur   #(on-update (:values @form-state))}}]]
          [:div.card-footer.clearfix.m-t-10]])])))
