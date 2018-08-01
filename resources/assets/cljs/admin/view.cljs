(ns admin.view
  (:require ["react-toastify" :as toastify]
            [reagent.core :as r]
            [re-frame.core :as rf]
            ;; components
            [admin.components.header.view :as header]
            [admin.components.breadcrumb.view :as breadcrumb]
            ;; compositions
            [admin.compositions.project.view :as project]
            [admin.compositions.projects.view :as projects]
            [admin.compositions.dashboard.view :as dashboard]
            [admin.compositions.galleries.view :as galleries]
            ))

(defn render-active-page
  "determine which composition to show"
  [active-page]
  (case active-page
    :projects  [projects/render]
    :project   [project/render]
    :galleries [galleries/render]
    [dashboard/render]))

(defn application
  "main entrance to the application"
  []
  (let [active-page       (rf/subscribe [:active-page])
        breadcrumb-items  (rf/subscribe [:breadcrumb])
        container-element (r/atom nil)
        scroll-handler    #(rf/dispatch-sync
                            [:save-scroll-top (-> % .-target .-scrollTop)])]
    (r/create-class
     {:component-did-mount
      (fn [self]
        (.addEventListener @container-element "scroll" scroll-handler))

      :component-will-unmount
      #(.removeEventListener @container-element "scroll" scroll-handler)

      :reagent-render
      (fn []
        [:<>
         [:> toastify/ToastContainer]
         [header/render]
         [:div.page-container.p-t-10
          {:ref #(reset! container-element %)}
          [:div.page-content-wrapper>div.content
           [:div.container.p-l-0.p-r-0.container-fixed-lg
            [breadcrumb/render @breadcrumb-items]]
           [:div.content-container.p-b-30
            (render-active-page @active-page)]]]])})))

