import useSWR from 'swr'

export function GetDeliveryInfo(id:string){
    const {data,error,mutate}=useSWR(`/api/WarpDelivery/deliveries/${id}`,
    (url)=>fetch(url).then(res => res.json()))
    return{
        data:data,
        isDataLoading: !error&&!data,
        isError: error,
    }
}