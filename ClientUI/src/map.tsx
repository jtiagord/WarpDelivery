import ReactMapboxGl from 'react-mapbox-gl'
import {useState,useEffect} from 'react'
import {Markers} from './marker'
import db from './firebase.config'

export function Map({token}:{token:string}){
    const [center,setCenter] = useState<Coordinates>({lat:-9.114954,long:38.756651})
    const [zoom, setZoom] = useState(17)
    const [isSearching,setIsSearching] = useState(true)

    const Map = ReactMapboxGl({
      accessToken:
        'pk.eyJ1IjoiYTQ0ODE2IiwiYSI6ImNrcmF2YnRmMzFsOHoycGxwb3h1bGswc2sifQ.qJqfMCqIvQ_5ctcCo2v4qg'
    }); 

    useEffect(()=>{
      getDeliveryInfo()
      async function getDeliveryInfo(){
        const pendingDelivery=db.collection('PENDING_DELIVERIES')
        
        pendingDelivery.doc(token).get()
        .then((doc)=>{
          if(doc.exists){
            setIsSearching(true)
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
      }
    },[])
    
return(
   <div>  
      <div>
        <Map
          style="mapbox://styles/mapbox/streets-v11"
          containerStyle={{
            height: '90vh',
            width: '100vw'
          }}
          center={[center.lat,center.long]}
          zoom={[zoom]}
        >
        <Markers token={token}/>
        </Map>
      </div>
    </div>
)
}