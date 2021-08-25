import db from './firebase.config'

const collections = {
    pending:db.collection('PENDING_DELIVERIES'),
    delivering:db.collection('DELIVERINGWARPERS')
}

export default collections