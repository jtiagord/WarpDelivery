import {BrowserRouter as Router,Route,Link,Switch} from "react-router-dom"
import { Home } from "./home";

function PageRouter(){
    return (
        <Router>
            <Switch>
            <Route path="/">
                <Home />
            </Route>
            </Switch>
        </Router>
    ); 
}

function App() {
    return (
        <PageRouter />
      )
}; 

export default App;