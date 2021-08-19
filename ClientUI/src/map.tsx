import ReactMapboxGl from 'react-mapbox-gl'
import {useState,useEffect} from 'react'
import {Markers} from './marker'
import db from './firebase.config'
import { getDoc } from './getDoc'

export function Map({token}:{token:string}){
    const [center,setCenter] = useState<Coordinates>({lat:null,long:null})
    const [zoom, setZoom] = useState(15)
    const [isSearching,setIsSearching] = useState(true)
    const [isCancelled,setIsCancelled] = useState(false)

    const Map = ReactMapboxGl({
      accessToken:
        'pk.eyJ1IjoiYTQ0ODE2IiwiYSI6ImNrcmF2YnRmMzFsOHoycGxwb3h1bGswc2sifQ.qJqfMCqIvQ_5ctcCo2v4qg'
    }); 

    useEffect(()=>{
      getDeliveryInfo()
      async function getDeliveryInfo(){
        const pendingDelivery=db.collection('PENDING_DELIVERIES')

        const delivering=db.collection('DELIVERINGWARPERS')
        
        pendingDelivery.doc(token).get()
        .then((doc)=>{
          if(!doc.exists){
            setIsSearching(false)
          }
        })

        pendingDelivery.onSnapshot((docSnapShot:any)=>{
          docSnapShot.docChanges().forEach((change:any)=>{
            if(change.type==='removed'&& change.doc.id===token){
              setIsSearching(false)
            }
          })
        }, err => {
          console.log(`Encountered error: ${err}`);
        })

        delivering.onSnapshot((docSnapShot:any)=>{
          docSnapShot.docChanges().forEach((change:any)=>{
            if(change.type==='removed'&& change.doc.data().delivery.id===token){
                setIsCancelled(true)
            }
          })
        }, err => {
          console.log(`Encountered error: ${err}`);
        })
      }
    },[])

    useEffect(()=>{
      if(!isSearching){
        checkIfCancelled()
      }

      async function checkIfCancelled(){
        const response=db.collection('DELIVERINGWARPERS')
        const query = response.where('delivery.id',"==",token)
        const data = await query.get()
        if(data.empty){
          setIsCancelled(true)
        }
        else{
          data.forEach(doc => {
            setCenter({lat:doc.data().location.latitude,long:doc.data().location.longitude})
          }); 
          setIsSearching(false)
        }
      }
    },[isSearching])
    
return(
  <div>
    {(() => {
      if(isSearching){
        return <p>Searching...</p>
      }
      else if(isCancelled){
        return <p>Your order has been cancelled by the store</p>
      }
      else{
        return <div>
            <Map
              style="mapbox://styles/mapbox/streets-v11"
              containerStyle={{
                height: '90vh',
                width: '100vw'
              }}
              center={[center.long,center.lat]}
              zoom={[zoom]}
            >
            <Markers token={token}/>
            </Map>
          </div>
      }
})()}
  </div>
)
}