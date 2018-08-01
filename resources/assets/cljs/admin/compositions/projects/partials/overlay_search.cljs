(ns admin.compositions.projects.partials.overlay-search
  (:require ["moment" :as moment]
            [reagent.core :as r]
            [clojure.string :as s]
            [admin.routes :as routes]
            [cljss.core :refer-macros [defstyles]]))

(defn- key-press-handler
  "global keydown handler that will trigger a search overlay
  when search is not visible and when the focus is not on
  any input or textarea on the document."
  [state show-search close-search]
  (let [ignored-tags #{"input" "textarea"}]
    (fn [e]
      (let [tag          (-> e .-target .-tagName s/lower-case)
            key          (.-which e)
            is-esc?      (= 27 key)
            is-meta?     (or (.-ctrlKey e) (.-metaKey e))
            is-alphabet? (or (and (> key 64) (< key 91))
                             (and (> key 96) (< key 123)))]
        ;; only try to show search when it's not visible
        ;; and when any alphabetical key is pressed
        (when (and (:is-visible? @state) is-esc?)
          (close-search))

        (when (and (not (:is-visible? @state))
                   ;; it has to be an alphabet press
                   is-alphabet?
                   ;; is shouldn't be a Ctrl + <key> or Alt + <key>
                   (not is-meta?))
          (when-not (contains? ignored-tags tag)
            (show-search (s/lower-case (.fromCharCode js/String key)))))))))

(defn- focused-input
  [_ on-change]
  (r/create-class
   {:component-did-mount
    #(.focus (r/dom-node %))

    :reagent-render
    (fn [value]
      [:input.no-border.overlay-search.bg-transparent
       {:id          :overlay-search
        :value       value
        :on-change   on-change
        :placeholder "Search..."}])}))

(defn- result-item
  "how to render each search result?"
  [{id         :id
    title      :title
    images     :images
    created-at :created_at}
   tabindex]
  [:div.col-6
   {:key       id
    :class     :search-result-item
    :tab-index tabindex}
   [:div
    [:div.thumbnail-wrapper.d48.circular.bg-success.text-white.inline.m-t-10
     [:img {:width  50
            :height 50
            :src    (:thumbnail images)}]]]

   [:div.p-l-10.inline
    [:h5.m-b-5
     [:a {:href (routes/project {:id id})}
      [:span.semi-bold.result-name title]]]
    [:p.hint-text "Created " (-> created-at :date moment .fromNow)]]])

(defn render
  [collection]
  (r/with-let [input-element   (r/atom nil)
               state           (r/atom {:value       ""
                                        :is-visible? false})
               hide-search     #(swap! state assoc
                                       :value ""
                                       :is-visible? false)
               show-search     (fn [initial-state]
                                 (swap! state assoc
                                        :value initial-state
                                        :is-visible? true))
               handler         (key-press-handler
                                state
                                show-search
                                hide-search)
               on-input-change #(swap! state assoc :value (-> % .-target .-value))
               _               (.addEventListener js/document "keydown" handler)]

    (when (:is-visible? @state)
      [:div.overlay
       {:data-pages :search}
       [:div.overlay-content.has-results.m-t-20
        [:div.container-fluid
         [:img.overlay-brand
          {:src    "/img/logo/logo-full-black.png"
           :alt    "Bravilogy"
           :height 80}]

         [:a.close-icon-light.overlay-close.text-black.fs-16
          {:href     "#"
           :on-click (fn [e] (.preventDefault e) (hide-search))}
          [:i.pg-close]]]
        [:div.container-fluid
         [focused-input (:value @state) on-input-change]

         [:div.inline-block
          [:p.fs-13
           [:i.fa.fa-search]  " searching within galleries"]]]
        [:div.container-fluid
         [:br]
         (when (not-empty (:value @state))
           (let [results (->> collection
                              (filter #(s/includes?
                                        (s/lower-case (:title %))
                                        (s/lower-case (:value @state)))))]
             (when (pos? (count results))
               [:div.search-results.m-t-20
                [:p.bold "Search results"]
                (into
                 [:div.row]
                 (map result-item results (range)))])))]]])

    (finally
      (.removeEventListener js/document "keydown" handler))))
