import collections from './dbCollections'

export function getDoc(token:string){
    return collections.delivering.where('delivery.id',"==",token).get()
}