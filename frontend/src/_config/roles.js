import CEData from "../containers/pages/Upload/CEData";
import Forenseq from "../containers/pages/Upload/Forenseq";
import FrequencyTable from "../containers/pages/Statistics/FrequencyTable/FrequencyTable";
import ChartStatistic from "../containers/pages/Statistics/ChartStatistic";
import Home from "../containers/pages/Welcome/Welcome";
import ISNPPage from "../containers/pages/Statistics/Protected/ISNPStats";
import Login from "../containers/pages/Login/Login";
import Map from "../containers/pages/Statistics/Map";
import Person from "../containers/pages/Person/Sample";
import PersonEdit from "../containers/pages/Person/PersonEdit";
import PersonList from "../containers/pages/Person/PersonList";
import PersonUpload from "../containers/pages/Upload/Person";
import Search from "../containers/pages/SearchPage/SearchPage";
import PatternAlignment from "../containers/pages/PatternAlignment/PatternAlignment";
import Signup from "../containers/pages/Signup/Signup";
import * as AdminPage from "../containers/admin/pages";
import {ADMIN_ROLE, GUEST_ROLE, LAB_USER_ROLE} from "./constants";
import AlleleDetails from "../containers/pages/Statistics/AlleleDetails";
import ExportAsExcel from "../containers/pages/Export/ExportAsExcel";
import ProfileSearchPage from "../containers/pages/SearchPage/ProfileSearchPage";
import MyProfile from "../containers/pages/Profile/MyProfile";
import ChangePassword from "../containers/pages/Profile/ChangePassword";

export const basicPageConfig = {
  login: {
    url: "/login",
    page: Login,
  },
  signup: {
    url: "/signup",
    page: Signup,
  },
}

export const dynamicPagesConfig = {
  home: {
    url: "/",
    page: Home,
  },
  search: {
    url: "/search",
    page: Search,
  },
  profileSearch: {
    url: "/profile-search",
    page: ProfileSearchPage,
  },
  graph: {
    url: "/stats/graph",
    page: ChartStatistic,
  },
  excelUpload: {
    url: "/upload/forenseq",
    page: Forenseq,
  },
  ceData: {
    url: "/upload/cedata",
    page: CEData,
  },
  personUpload: {
    url: "/upload/person",
    page: PersonUpload,
  },
  patternAlignment: {
    url: "/full/pattern-alignment",
    page: PatternAlignment,
  },
  exportAsExcel: {
    url: "/export/excel",
    page: ExportAsExcel,
  },
  isnp: {
    url: "/stats/isnp",
    page: ISNPPage,
  },
  personList: {
    url: "/persons",
    page: PersonList,
  },
  map: {
    url: "/stats/map",
    page: Map,
  },
  personEdit: {
    url: "/persons/:id",
    page: PersonEdit,
  },
  personView: {
    url: "/samples/:id/forenseq",
    page: Person,
  },
  myProfile: {
    url: "/profile",
    page: MyProfile,
  },
  changePwd: {
    url: "/change-password",
    page: ChangePassword,
  },
  freqTable: {
    url: "/stats/freq-table",
    page: FrequencyTable,
  },
  manageAllUser: {
    url: "/admins/users",
    page: AdminPage.ManageAllUser
  },
  manageNewUser: {
    url: "/admins/users/new",
    page: AdminPage.ManageNewUser
  },
  manageLocus: {
    url: "/admins/locus",
    page: AdminPage.ManageLocus
  },
  manageRaces: {
    url: "/admins/races",
    page: AdminPage.ManageRace
  },
  manageMap: {
    url: "/admins/map",
    page: AdminPage.ManageMap
  },
  managePatternAlignment: {
    url: "/admins/pattern-alignment",
    page: AdminPage.ManageSeqAlign
  },
  manageCoreLoci: {
    url: "/admins/core-loci",
    page: AdminPage.ManageCoreLoci
  },
  manageStatsProfile: {
    url: "/admins/stats-profile",
    page: AdminPage.ManageStatisticProfile
  },
  manageCalculationProfileSearch: {
    url: "/admins/calculation-profile",
    page: AdminPage.EditCalculationProfileSearch
  },
  manageUploadRefPattern: {
    url: "/admins/reference-patterns",
    page: AdminPage.UploadReferencePatterns
  },
  overallSeqPattern: {
    url: "/stats/overview-str",
    page: AlleleDetails
  },
  manageCountry: {
    url: "/admin/manage-countries",
    page: AdminPage.ManageCountry
  },
  manageProvinces: {
    url: "/admin/:countryId/manage-provinces",
    page: AdminPage.ManageProvinces
  },
  manageRegions: {
    url: "/admin/:countryId/edit-provinces",
    page: AdminPage.EditProvinces
  }
};

const roles = {
  [GUEST_ROLE]: [
    ...Object.values(basicPageConfig),
    dynamicPagesConfig.home,
    dynamicPagesConfig.search,
    dynamicPagesConfig.graph,
    dynamicPagesConfig.map,
    dynamicPagesConfig.freqTable,
    dynamicPagesConfig.isnp,
    dynamicPagesConfig.overallSeqPattern,
    dynamicPagesConfig.profileSearch
  ],
  [LAB_USER_ROLE]: [
    ...Object.values(basicPageConfig),
    dynamicPagesConfig.home,
    dynamicPagesConfig.search,
    dynamicPagesConfig.graph,
    dynamicPagesConfig.excelUpload,
    dynamicPagesConfig.ceData,
    dynamicPagesConfig.personUpload,
    dynamicPagesConfig.isnp,
    dynamicPagesConfig.exportAsExcel,
    dynamicPagesConfig.personList,
    dynamicPagesConfig.map,
    dynamicPagesConfig.personEdit,
    dynamicPagesConfig.personView,
    dynamicPagesConfig.freqTable,
    dynamicPagesConfig.patternAlignment,
    dynamicPagesConfig.overallSeqPattern,
    dynamicPagesConfig.profileSearch,
    dynamicPagesConfig.myProfile,
    dynamicPagesConfig.changePwd,
  ],
  [ADMIN_ROLE]: [
    ...Object.values(basicPageConfig),
    ...Object.values(dynamicPagesConfig),
  ],
};

export default roles;
