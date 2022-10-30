import React, {useEffect, useState} from "react";
import routeConfig from "../../_config/roles";
import NotFoundPage from "../../containers/pages/NotFound/NotFound";
import {Route, Switch} from "react-router-dom";
import UserNavbar from "../navbar/NavBar";
import {GUEST_ROLE} from "../../_config/constants";
import LocalStorageService from "../../_services/LocalStorageService";
import menus from "../../_config/menu";
import axios from "../../_config/axios";

function PrivateRoutes(props) {
  const [role, setRole] = useState(props.role || "guest");
  const [currentMenu, setCurrentMenu] = useState("");
  const [menuNotification, setMenuNotification] = useState({});

  const allowedRoutes = routeConfig[role];
  const menuConfig = {position: "fixed", top: 0, width: "100%", overflow: "hidden", zIndex: 2};
  const pathname = window.location.pathname

  function fetchMenuDetails() {
    menus.forEach(menu => {
      if (menu.notificationURL && menu.roles.includes(role)) {
        axios.get(menu.notificationURL).then(res => {
          const keysMap = menu.notification(res.data);
          setMenuNotification(curNoti => ({...curNoti, [menu.key]: keysMap}))
        })
      }
    })
  }

  useEffect(() => {
    setCurrentMenu(pathname)
    fetchMenuDetails();
  }, [])

  return (
      <div style={{marginBottom: "10px"}}>
        <div style={menuConfig}>
          <UserNavbar
              menus={menus}
              currentMenu={currentMenu}
              setCurrentMenu={setCurrentMenu}
              menuNotification={menuNotification}
              setRole={setRole}
              showLogin={GUEST_ROLE === LocalStorageService.getRole()}
          />
        </div>
        <div style={{paddingTop: "80px"}}>
          <Switch>
            {allowedRoutes.map((route) => (
                <Route key={route.url} exact path={route.url}>
                  <route.page
                      currentMenu={currentMenu}
                      setCurrentMenu={setCurrentMenu}
                      menuNotification={menuNotification}
                      setRole={setRole}
                      fetchMenuDetails={fetchMenuDetails}
                  />
                </Route>
            ))}
            <Route component={NotFoundPage}/>
          </Switch>
        </div>
      </div>
  )
}

export default PrivateRoutes;
