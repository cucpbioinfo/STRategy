import React from "react";
import {Badge, Button, Menu} from "antd";
import {LoginOutlined, LogoutOutlined,} from "@ant-design/icons";
import {Link, useHistory} from "react-router-dom";
import LocalStorageService from "../../_services/LocalStorageService";
import {GUEST_ROLE} from "../../_config/constants";

const SubMenu = Menu.SubMenu;
const MenuItemGroup = Menu.ItemGroup;

export default function UserNavbar(props) {
  const {menuNotification, currentMenu, setCurrentMenu, showLogin, menus} = props;
  const history = useHistory();

  const handleMenuClick = ({key}) => {
    if (key === "logout") {
      LocalStorageService.removeToken();
      props.setRole(GUEST_ROLE);
      history.push("/")
    } else {
      setCurrentMenu(key);
    }
  };

  function renderSubMenus(menu, notification) {
    let totalCount = 0;

    if (notification) {
      Object.values(notification).forEach(count => {
        totalCount += count;
      })
    }

    const MenuIcon = menu.icon

    let title = (
        <span>
          <MenuIcon/> {menu.name}
        </span>
    );

    if (totalCount !== 0) {
      title = (
          <span>
            <Badge count={totalCount}>
              <MenuIcon/> {menu.name}
            </Badge>
          </span>
      )
    }

    return (
        <SubMenu
            key={menu.key}
            title={title}
        >
          {menu?.submenuGroup.map(menuGroup => (
              <MenuItemGroup key={menuGroup.key ?? menuGroup.label} title={menuGroup.label}>
                {menuGroup.menus.map(listMenu => {
                      if (notification && notification[listMenu.key]) {
                        return (
                            <Menu.Item key={listMenu.key ?? listMenu.link}>
                              <Link to={listMenu.link}>
                                <Badge key={listMenu.key ?? listMenu.link} count={notification[listMenu.key]}>
                                  <listMenu.icon/> {listMenu.title}
                                </Badge>
                              </Link>
                            </Menu.Item>
                        )
                      }
                      return (
                          <Menu.Item key={listMenu.key ?? listMenu.link}>
                            <Link to={listMenu.link}>
                              <listMenu.icon/> {listMenu.title}
                            </Link>
                          </Menu.Item>
                      )
                    }
                )}
              </MenuItemGroup>
          ))}
        </SubMenu>
    )
  }

  return (
      <Menu
          onClick={handleMenuClick}
          selectedKeys={[currentMenu]}
          mode="horizontal"
          theme="light"
          style={{lineHeight: "64px"}}
      >
        {menus.map(menu => {
          const allowedRoles = menu.roles
          if (!allowedRoles || !allowedRoles.includes(LocalStorageService.getRole())) {
            return null
          }
          const MenuIcon = menu.icon;
          if (menu.submenuGroup) {
            return renderSubMenus(menu, menuNotification[menu.key]);
          }
          return (
              <Menu.Item key={menu.key ?? menu.link}>
                <Link to={menu.link}>
                  <MenuIcon/> {menu.name}
                </Link>
              </Menu.Item>
          )
        })}
        {showLogin ? (
            <Menu.Item key="login">
              <Button type="dashed">
                <Link to="/login">
                  <LoginOutlined/> Login
                </Link>
              </Button>
            </Menu.Item>
        ) : (
            <Menu.Item key="logout">
              <Button type="default">
                <LogoutOutlined/> Logout
              </Button>
            </Menu.Item>
        )}
      </Menu>
  );
}
