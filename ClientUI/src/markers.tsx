import {Layer,Feature,Popup} from 'react-mapbox-gl'
import {useState,useEffect} from 'react'
import collections from './dbCollections'
import { getDoc } from './getDoc'

type MarkerInfo = {
  id:string,
  data:any
}

export function Markers({id,data}:MarkerInfo){
    const [warperCoord,setWarperCoord] = useState<Coordinates>({lat:null,long: null})
    const [clientCoord,setClientCoord] = useState<Coordinates>({lat:null,long:null})
    const [storeCoord,setStoreCoord] = useState<Coordinates>({lat:null,long:null})
    const [popUpVisibility,setPopUpVisibility] = useState(0)

    useEffect(() => {
      getDeliveryInfo()

      async function getDeliveryInfo(){
        const query = collections.delivering.where('delivery.id',"==",id)
        
        const data = await getDoc(id)

        if (data.empty) {
          console.log('Sorry, we could not find a delivery matching that id.');
          return;
        } 

        data.forEach(doc => {
          setClientCoord({lat:doc.data().delivery.deliveryLocation.latitude,long:doc.data().delivery.deliveryLocation.longitude})
          setStoreCoord({lat:doc.data().delivery.pickUpLocation.latitude,long:doc.data().delivery.pickUpLocation.longitude})
        }); 

        const listener = await query.onSnapshot((docSnapShot:any)=>{
          docSnapShot.docChanges().forEach((change:any)=>{
            setWarperCoord({lat:change.doc.data().location.latitude,long:change.doc.data().location.longitude})
          })
        }, err => {
          console.log(`Encountered error: ${err}`);
        })
      }
    }, [])
       
return(
   <div>

      <Layer type="circle" id="marker" paint={{
                'circle-color': "#ff5200",
                'circle-stroke-width': 5,
                'circle-stroke-color': '#ff5200',
                'circle-stroke-opacity': 1
              }}>
        <Feature coordinates={[warperCoord.long,warperCoord.lat]}
        onClick={()=>{setPopUpVisibility(1)}}/>
        <Feature coordinates={[clientCoord.long,clientCoord.lat]}/>
        <Feature coordinates={[storeCoord.long,storeCoord.lat]}
        onClick={()=>{setPopUpVisibility(2)}}/>
      </Layer>

      <Layer type="symbol" layout={{
                'text-field': 'Warper',
                'text-font': ['Open Sans Bold', 'Arial Unicode MS Bold'],
                'text-size': 11,
                'text-transform': 'uppercase',
                'text-letter-spacing': 0.05,
                'text-offset': [0, 1.5]
              }}>
        <Feature coordinates={[warperCoord.long,warperCoord.lat]}/>
      </Layer>

      <Layer type="symbol" layout={{
                'text-field': 'Loja',
                'text-font': ['Open Sans Bold', 'Arial Unicode MS Bold'],
                'text-size': 11,
                'text-transform': 'uppercase',
                'text-letter-spacing': 0.05,
                'text-offset': [0, 1.5]
              }}>
        <Feature coordinates={[storeCoord.long,storeCoord.lat]}/>
      </Layer>

      <Layer type="symbol" layout={{
                'text-field': 'Cliente',
                'text-font': ['Open Sans Bold', 'Arial Unicode MS Bold'],
                'text-size': 11,
                'text-transform': 'uppercase',
                'text-letter-spacing': 0.05,
                'text-offset': [0, 1.5]
              }}>
        <Feature coordinates={[clientCoord.long,clientCoord.lat]}/>
      </Layer>
      {popUpVisibility == 1?
        <Popup
        coordinates={[warperCoord.long,warperCoord.lat]}
      >
        <p>{data.warper.firstname} {data.warper.lastname}</p>
        <p>{data.warper.phonenumber}</p>
      </Popup>:
        popUpVisibility==2?
        <Popup
          coordinates={[storeCoord.long,storeCoord.lat]}
        >
          <p>{data.store.name}</p>
          <p>{data.store.postalcode}</p>
          <p>{data.store.address}</p>
        </Popup>:
        null
      }
    </div>
)
}