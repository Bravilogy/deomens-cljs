(ns admin.components.collection.view
  (:require [re-frame.core :as rf]
            [admin.components.sortable-list.view :as sortable-list]))

(defn- empty-gallery
  []
  [:div.card.card-default
   [:div.card-block
    [:div.hint-text "Nothing to show here."]]])

(defn render
  [{collection-id         :collection_id
    collection-order-path :collection-order-path}]
  (let [is-updating? (rf/subscribe [:is-loading? :collections collection-id])
        sync-order   #(rf/dispatch [:sync-collection-order
                                    collection-id
                                    collection-order-path
                                    ;; we will receive a new order here
                                    %])]
    (fn [{collection-id :collection_id
         items         :items
         :as           params}]
      ;; we want to keep the rest of params in place
      ;; we just want to pass down a few of our own
      ;; with a higher priority (will overwrite)
      (if (pos? (count items))
        [sortable-list/render (merge
                               params
                               ;; attach the handler only when there is a
                               ;; collection id. otherwise there is no re-order
                               {:sync-order   (when collection-id sync-order)
                                :is-updating? @is-updating?})]
        [empty-gallery]))))
