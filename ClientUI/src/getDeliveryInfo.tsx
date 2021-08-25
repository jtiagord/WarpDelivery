import useSWR from 'swr'

export function GetDeliveryInfo(id:string){
    const {data,error,mutate}=useSWR(`/api/WarpDelivery/deliveries/${id}`,
    (url)=>fetch(url).then(res => res.json()), { refreshInterval: 2000 })
    return{
        deliveryData:data,
        isDataLoading: !error&&!data,
        isError: error,
    }
}