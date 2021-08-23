import db from './firebase.config'

export function getDoc(token:string){
    const response=db.collection('DELIVERINGWARPERS')
    const query = response.where('delivery.id',"==",token)

    return query.get()
}