import ReactMapboxGl,{Layer,Feature} from 'react-mapbox-gl'
import React,{useState,useEffect} from 'react'
const markerUrl = 'https://lh3.googleusercontent.com/proxy/02fLLeFaaVq-tKSbqajs3nsVTsuENFgGpxafRXm8g8fyzFIXaDXv5qdgHb1igdop7z1oiTEHBXoVUYJ75-o7ndBrs1fb8u3_axJ_ZNXvhuy_QavZlOsBUg'

export function Marker({token}:{token:string}){
    const [warperCoord,setWarperCoord] = useState<Coordinates>({lat:-9.114954,long:38.756651})
    const [clientCoord,setClientCoord] = useState<Coordinates>({lat:-9.115602,long:38.758436})
    const [storeCoord,setStoreCoord] = useState<Coordinates>({lat:-9.111257,long:38.755106})
    const [count,setCount] = useState(0)
    const coordinates=[
      {lat: -9.115439,
      long:38.756821},

      {lat: -9.116044,
      long:38.757039},

      {lat: -9.116428,
      long:38.757097},

      {lat: -9.117136,
      long:38.757206}
    ]
    
    //Marker animation test
    useEffect(() => {
      const interval = setInterval(() => {
        let newCoord = coordinates[count]
        setWarperCoord({lat:newCoord.lat,long:newCoord.long})
        setCount(count=>count+1)
        console.log(count)
      }, 3000);
      return () => clearInterval(interval);
    }, [count]);
       
return(
   <div>
    <Layer type="circle" id="marker" paint={{
        'circle-color': "#ff5200",
        'circle-stroke-width': 10,
        'circle-stroke-color': '#ff5200',
        'circle-stroke-opacity': 1
      }}>
        <Feature coordinates={[warperCoord.lat,warperCoord.long]}/>
        <Feature coordinates={[clientCoord.lat, clientCoord.long]}/>
        <Feature coordinates={[storeCoord.lat, storeCoord.long]}/>
      </Layer>
    </div>
)
}