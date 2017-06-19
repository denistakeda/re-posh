[![Clojars Project](https://img.shields.io/clojars/v/re-posh.svg)](https://clojars.org/re-posh)
[![Gitter](https://img.shields.io/gitter/room/thunder-project/thunder.svg?style=flat-square)](https://gitter.im/re-posh/Lobby?utm_source=share-link&utm_medium=link&utm_campaign=share-link)

# re-posh

`re-posh` is a ClojureScript library that empowers you to improve your [re-frame](https://github.com/Day8/re-frame) single-page applications to have a robust and flexible in-page database to manage your application's state. 

You can also leverage [DatSync](https://github.com/metasoarous/datsync) to have that in-browser app sync to a server running [Datomic](http://www.datomic.com/) or [DataScript](https://github.com/tonsky/datascript) to simply store your data, or for realtime collaboration between multiple clients.

re-frame is a [reactive programming](https://gist.github.com/staltz/868e7e9bc2a7b8c1f754) library for writing single-page apps in ClojureScript 
 using [Reagent](https://github.com/reagent-project/reagent), which wraps Facebook's popular [React](https://facebook.github.io/react/) library for building component-oriented user interfaces.

[Posh](https://github.com/mpdairy/posh) improves Reagent to allow the declarative binding of user interface components to a local DataScript database. Like Datomic, DataScript supports the powerful `q` and `pull` query API's, and [Datalog](http://www.learndatalogtoday.org/) in general. 

`re-posh` allows Posh and re-frame to work together by adding support for re-frame specific `subscriptions`, `events`, `effects`, and `co-effects` to Posh.

## Why?

State management within *any* application, if treated as a secondary concern, can become a creeping problem that becomes a source of difficult-to-debug problems as the app grows in complexity. re-frame offers a solution to that problem by [having a single app-db data structure](https://github.com/Day8/re-frame/blob/master/docs/ApplicationState.md) in the form of a `reagent/atom` as a single source of truth in the app. UI elements subscribe to changes to that structure and create events that mutate that structure. 
 
 `re-posh` replaces that single `atom` with a full-featured in-memory database. Now, your application's state has sophisticated data management and querying capabilities that defy complexity. UI elements can bind to the db with all of the expressiveness of Datalog, and the app maintains a single source of truth with an *actual* database. 
 
## Usage

Require `re-posh` in your app:
```clojure
(ns example
  (:require [reagent.core :as r]
            [re-posh.core :refer [connect! reg-query-sub reg-pull-sub reg-event-ds]]
            [datascript.core :as d]))
```

## Connection

Connect your DataScript database to `re-posh`:

```clojure
(ns example.db
  (:require
    [datascript.core    :as d]
    [re-posh.core       :refer [connect!]]))

(def conn (d/create-conn))
(connect! conn)
```

## Subscriptions

You can subscribe to the DataScript database with a query subscription or with a pull subscription.

### Query subscription

You can use `reg-query-sub` function for subscribe to any query

```clojure
(reg-query-sub
  :task-ids
  '[ :find  [?tid ...]
     :where [?tid :task/title]])
```

This function takes two params, subscription name and datalog [query](http://docs.datomic.com/query.html#sec-5). You can use this subscription as regular re-frame subscription

```clojure
(defn page []
  (let [task-ids (subscribe [:task-ids])]
    (fn []
       ...
```

Every parameter in a signal will be pass as param to the query

```clojure
(reg-query-sub
   :task-ids
   '[ :find  [?tid ...]
      :in $ ?param-1 ?param-2
      :where ...

(let [task-ids (subscribe [:task-ids param-1 param-2])]
   ...)
```

### Pull subscription

Pull subscriptions creates subscription to the entity. `reg-pull-sub` function create pull subscription and takes two params, subscription name and pull pattern. For more details see [Datomic Pull](http://docs.datomic.com/pull.html)

```clojure
(reg-pull-sub
   :sub-name
   '[*])

 ;; Usage

 (let [entity-id 123
       entity    (subscribe [:sub-name entity-id])])
 ```

 ## Events

`re-posh` uses totally the same solution as re-frame `reg-event-db` but with datascript database instead. Function `reg-event-ds` takes event name and event handler. First param for handler is a dereferenced DataScript database. You can do with it whatewer you like, make query or take entities with pull. The second parameter is a signal. Event handler have to return transaction.

 ```clojure
 (reg-event-ds
  :update-task
  (fn [ds [_ id path value]] ;; ds is not used here, just an example
    [[:db/add id path value]]))
 ```

 ## Effects and Co-effects

`re-posh` introduce one effect and one co-effect. You can use them as regular re-frame effects and co-effects (in fact they are regular re-frame effects and coeffects)


### Transact effect

This effect commit transaction into the DataScript database

```clojure
(ns example.events
   (:require [re-frame.core :as r]))

(r/reg-event-fx
   :my-event
   (fn [cofx [_ id k v]]
      {:transact [[:db/add id k v]]})) ;; return datascript transaction
```

### DS co-effect

This co-effect provide DataScript database into your event handler

```clojure
(ns example.events
   (:require [re-frame.core :as r]))

(r/reg-event-fx
   :my-event
   [(r/inject-cofx :ds)] ;; inject coeffect
   (fn [{:keys [ds]} [_ id k v]] ;; ds here is the DataScript database
      {:transact [[:db/add id k v]]}))
```

## Contribution

Pull requests are welcome. Email me on <denis.takeda@gmail.com> if you have any questions, suggestions or proposals.

 ## License

 Copyright © 2017 Denis Krivosheev

 Distributed under the MIT License

