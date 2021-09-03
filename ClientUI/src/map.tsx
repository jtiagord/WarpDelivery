import ReactMapboxGl from 'react-mapbox-gl'
import {useState,useEffect} from 'react'
import {Markers} from './markers'
import { getDoc } from './getDoc'
import collections from './dbCollections'

export function Map({token}:{token:string}){
    const [center,setCenter] = useState<Coordinates>({lat:null,long:null})
    const [zoom, setZoom] = useState(15)
    const [state, setState ] = useState<string>()
    const [deliveryData, setDeliveryData ] = useState(null)

    
function fetchAPIData(){
  fetch(`/api/WarpDelivery/deliveries/${token}`)
        .then(res=>res.json())
        .then(jSONRes=>{
          setDeliveryData(jSONRes)
          setState(jSONRes.state)
        })
        .catch(err=>console.log("error while fetching API data:" + err))
}

   useEffect(()=>{
    fetchAPIData()

    let unmounted=false

      collections.delivering.onSnapshot((docSnapShot:any)=>{
          docSnapShot.docChanges().forEach((change:any)=>{
            if(!unmounted && change.type!=='removed' && change.doc.data().delivery.id===token){
              setState(change.doc.data().state)
            }
          })
        }, err => {
          console.log(`Encountered error: ${err}`);
        })

        collections.delivering.onSnapshot((docSnapShot:any)=>{
          docSnapShot.docChanges().forEach((change:any)=>{
            if(!unmounted && change.type==='removed' && change.doc.data().delivery.id===token){
              fetchAPIData()
            }
          })
        }, err => {
          console.log(`Encountered error: ${err}`);
        })
      return () => {unmounted=true}
    },[])

    useEffect(()=>{
      if(state==='DELIVERING')
        setMapCenter()

      async function setMapCenter(){
        let unmounted=false
        const data = await getDoc(token)
        data.forEach(doc => {
            if(!unmounted)
            setCenter({lat:doc.data().location.latitude,long:doc.data().location.longitude})
          }); 
        return () => {unmounted=true}
      }
    },[state])

    const Map = ReactMapboxGl({
      accessToken:
        'pk.eyJ1IjoiYTQ0ODE2IiwiYSI6ImNrcmF2YnRmMzFsOHoycGxwb3h1bGswc2sifQ.qJqfMCqIvQ_5ctcCo2v4qg'
    }); 
    
return(
  <div>
    {(() => {
      console.log(state)
        if(state==='RETRIEVING' || state==='LOOKING_FOR_WARPER'){
          return <p>We're preparing your delivery...</p>
        }
        else if(state==='CANCELLED'){
          return <p>Your order has been cancelled by the store</p>
        }
        else if(state==='DELIVERED'){
          return <p>Your order has arrived!</p>
        }
        else if(state==='DELIVERING'){
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
              <Markers id={token} deliveryData={deliveryData}/>
              </Map>
            </div>
        }
})()}
  </div>
)
}