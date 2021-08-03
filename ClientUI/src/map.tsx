import ReactMapboxGl,{Layer,Source,Marker,Feature} from 'react-mapbox-gl'
import React,{useState,useRef,useEffect} from 'react'
const markerUrl = 'https://lh3.googleusercontent.com/proxy/02fLLeFaaVq-tKSbqajs3nsVTsuENFgGpxafRXm8g8fyzFIXaDXv5qdgHb1igdop7z1oiTEHBXoVUYJ75-o7ndBrs1fb8u3_axJ_ZNXvhuy_QavZlOsBUg'

type Coordinates={
  lat:number,
  long:number
}

export function Map({token}:{token:string}){
    const [warperCoord,setWarperCoord] = useState<Coordinates>({lat:-9.114954,long:38.756651})
    const [clientCoord,setClientCoord] = useState<Coordinates>({lat:-9.115602,long:38.758436})
    const [storeCoord,setStoreCoord] = useState<Coordinates>({lat:-9.111257,long:38.755106})
    const [zoom, setZoom] = useState(17)
    const [count,setCount] = useState(0)
    /*const coordinates=[
      {lat: -9.115439,
      long:38.756821},

      {lat: -9.116044,
      long:38.757039},

      {lat: -9.116428,
      long:38.757097},

      {lat: -9.117136,
      long:38.757206}
    ]
    
    useEffect(() => {
      const interval = setInterval(() => {
        let newCoord = coordinates[count]
        setWarperCoord({lat:newCoord.lat,long:newCoord.long})
        setCount(count=>count+1)
        console.log(count)
      }, 3000);
      return () => clearInterval(interval);
    }, []);*/

    const Map = ReactMapboxGl({
      accessToken:
        'pk.eyJ1IjoiYTQ0ODE2IiwiYSI6ImNrcmF2YnRmMzFsOHoycGxwb3h1bGswc2sifQ.qJqfMCqIvQ_5ctcCo2v4qg'
    });
    
    
return(
   <div>
    <Map
      style="mapbox://styles/mapbox/streets-v11"
      containerStyle={{
        height: '90vh',
        width: '100vw'
      }}
      center={[-9.114954,38.756651]}
      zoom={[zoom]}
    >
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
    </Map>
    </div>
)
}