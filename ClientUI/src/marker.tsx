import ReactMapboxGl,{Layer,Feature} from 'react-mapbox-gl'
import React,{useState,useEffect} from 'react'
import db from './firebase.config'
const markerUrl = 'https://lh3.googleusercontent.com/proxy/02fLLeFaaVq-tKSbqajs3nsVTsuENFgGpxafRXm8g8fyzFIXaDXv5qdgHb1igdop7z1oiTEHBXoVUYJ75-o7ndBrs1fb8u3_axJ_ZNXvhuy_QavZlOsBUg'

export function Marker({token}:{token:string}){
    const [warperCoord,setWarperCoord] = useState<Coordinates>({lat:null,long: null})
    const [clientCoord,setClientCoord] = useState<Coordinates>({lat:null,long:null})
    const [storeCoord,setStoreCoord] = useState<Coordinates>({lat:null,long:null})
    //Marker animation test
    /*useEffect(() => {
      const interval = setInterval(() => {
        let newCoord = coordinates[count]
        setWarperCoord({lat:newCoord.lat,long:newCoord.long})
        setCount(count=>count+1)
        console.log(count)
      }, 3000);
      return () => clearInterval(interval);
    }, [count]);*/

    useEffect(() => {
      getDeliveryInfo()

      async function getDeliveryInfo(){
        const response=db.collection('DELIVERINGWARPERS')
        const data = await response.where('token',"==",token).get()
        if (data.empty) {
          console.log('Sorry, we could not find a delivery matching that token.');
          return;
        }  
        data.forEach(doc => {
          setWarperCoord({lat:doc.data().location.latitude,long:doc.data().location.longitude})
          setClientCoord({lat:doc.data().delivery.deliveryLocation.latitude,long:doc.data().delivery.deliveryLocation.longitude})
          setStoreCoord({lat:doc.data().delivery.pickUpLocation.latitude,long:doc.data().delivery.pickUpLocation.longitude})
        });
      }
    }, [])
       
return(
   <div>
    <Layer type="circle" id="marker" paint={{
        'circle-color': "#ff5200",
        'circle-stroke-width': 10,
        'circle-stroke-color': '#ff5200',
        'circle-stroke-opacity': 1
      }}>
        <Feature coordinates={[warperCoord.long,warperCoord.lat]}/>
        <Feature coordinates={[clientCoord.long, clientCoord.lat]}/>
        <Feature coordinates={[storeCoord.long, storeCoord.lat]}/>
      </Layer>
    </div>
)
}