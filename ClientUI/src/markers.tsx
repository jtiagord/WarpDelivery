import {Layer,Feature,Popup} from 'react-mapbox-gl'
import {useState,useEffect} from 'react'
import collections from './dbCollections'
import { getDoc } from './getDoc'
import { GetDeliveryInfo } from './getDeliveryInfo'

export function Markers({id}:{id:string}){
    const [warperCoord,setWarperCoord] = useState<Coordinates>({lat:null,long: null})
    const [clientCoord,setClientCoord] = useState<Coordinates>({lat:null,long:null})
    const [storeCoord,setStoreCoord] = useState<Coordinates>({lat:null,long:null})
    const [storePopupVisibility,setStorePopupVisibility] = useState(false)
    const [warperPopupVisibility,setWarperPopupVisibility] = useState(false)
    const {data,isError} = GetDeliveryInfo(id)

    useEffect(() => {
      getDeliveryInfo()

      async function getDeliveryInfo(){
        let unmounted = false
        const query = collections.delivering.where('delivery.id',"==",id)
        
        const data = await getDoc(id)

        if (data.empty) {
          console.log('Sorry, we could not find a delivery matching that id.');
          return;
        } 

        data.forEach(doc => {
          if(!unmounted){
          setClientCoord({lat:doc.data().delivery.deliveryLocation.latitude,long:doc.data().delivery.deliveryLocation.longitude})
          setStoreCoord({lat:doc.data().delivery.pickUpLocation.latitude,long:doc.data().delivery.pickUpLocation.longitude})
          }
        }); 

        const listener = query.onSnapshot((docSnapShot:any)=>{
          docSnapShot.docChanges().forEach((change:any)=>{
            if(!unmounted)
              setWarperCoord({lat:change.doc.data().location.latitude,long:change.doc.data().location.longitude})
          })
        }, err => {
          console.log(`Encountered error: ${err}`);
        })

        return () => {unmounted=true}
      }
    }, [])

    if(isError) return <div>failed to load</div>
    if (!data) return <div>loading...</div>
       
return(
   <div>

      <Layer type="circle" id="marker" paint={{
                'circle-color': "#ff5200",
                'circle-stroke-width': 5,
                'circle-stroke-color': '#ff5200',
                'circle-stroke-opacity': 1
              }}>
        <Feature coordinates={[warperCoord.long,warperCoord.lat]}
        onClick={()=>{setWarperPopupVisibility(!warperPopupVisibility)}}/>
        <Feature coordinates={[clientCoord.long,clientCoord.lat]}/>
        <Feature coordinates={[storeCoord.long,storeCoord.lat]}
        onClick={()=>{setStorePopupVisibility(!storePopupVisibility)}}/>
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
      {warperPopupVisibility?
        <Popup
        coordinates={[warperCoord.long,warperCoord.lat]}
      >
        <p>{data.warper.firstname} {data.warper.lastname}</p>
        <p>{data.warper.phonenumber}</p>
      </Popup>: null}
        {storePopupVisibility?
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