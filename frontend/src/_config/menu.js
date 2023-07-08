import {dynamicPagesConfig} from "./roles";
import {
  BarChartOutlined,
  BarcodeOutlined,
  BoxPlotOutlined,
  CalculatorOutlined,
  CloudUploadOutlined,
  FileAddOutlined,
  FileExcelOutlined,
  FileSearchOutlined,
  GlobalOutlined,
  GoogleOutlined,
  HomeOutlined,
  IdcardOutlined,
  PartitionOutlined,
  ProfileOutlined,
  ProjectOutlined,
  RadarChartOutlined,
  ReconciliationOutlined,
  RetweetOutlined,
  SearchOutlined,
  SettingOutlined,
  SlidersOutlined,
  TableOutlined,
  TabletOutlined,
  TeamOutlined,
  UnorderedListOutlined,
  UsergroupAddOutlined,
  UserOutlined,
  UserSwitchOutlined
} from "@ant-design/icons";
import {ADMIN_ROLE, GUEST_ROLE, LAB_USER_ROLE} from "./constants";

const menus = [
  {
    name: "Home",
    key: "Home",
    roles: [GUEST_ROLE, LAB_USER_ROLE, ADMIN_ROLE],
    link: dynamicPagesConfig.home.url,
    icon: HomeOutlined,
    submenuGroup: null,
  },
  {
    name: "Search",
    key: "Search",
    roles: [GUEST_ROLE, LAB_USER_ROLE, ADMIN_ROLE],
    link: null,
    icon: FileSearchOutlined,
    submenuGroup: [
      {
        label: "Search function",
        menus: [
          {
            title: "Locus Search",
            link: dynamicPagesConfig.search.url,
            key: dynamicPagesConfig.search.url,
            icon: BarChartOutlined,
          },
          {
            title: "Profile Search",
            link: dynamicPagesConfig.profileSearch.url,
            key: dynamicPagesConfig.profileSearch.url,
            icon: TabletOutlined,
          }
        ]
      }
    ],
  },
  {
    name: "Statistics",
    key: "Statistics",
    roles: [GUEST_ROLE, LAB_USER_ROLE, ADMIN_ROLE],
    link: null,
    icon: RadarChartOutlined,
    submenuGroup: [
      {
        label: "STRategy DB",
        menus: [
          {
            title: "Genetic variation",
            link: dynamicPagesConfig.graph.url,
            key: dynamicPagesConfig.graph.url,
            icon: SearchOutlined,
          },
          {
            title: "Allele details",
            link: dynamicPagesConfig.overallSeqPattern.url,
            key: dynamicPagesConfig.overallSeqPattern.url,
            icon: TabletOutlined,
          },
          {
            title: "Allele distribution by geolocation",
            link: dynamicPagesConfig.map.url,
            key: dynamicPagesConfig.map.url,
            icon: GoogleOutlined,
          },
          {
            title: "iSNPs",
            link: dynamicPagesConfig.isnp.url,
            key: dynamicPagesConfig.isnp.url,
            icon: BoxPlotOutlined,
          },
        ]
      },
      {
        label: "Global Database",
        menus: [
          {
            title: "STRategy DB & STRidER allele frequency comparison",
            link: dynamicPagesConfig.freqTable.url,
            key: dynamicPagesConfig.freqTable.url,
            icon: TableOutlined,
          },
        ]
      },
    ],
  },
  {
    name: "Lab User Menu",
    key: "Lab User Menu",
    roles: [LAB_USER_ROLE, ADMIN_ROLE],
    link: null,
    icon: SettingOutlined,
    submenuGroup: [
      {
        label: "Manage samples",
        menus: [
          {
            title: "Manage person",
            link: dynamicPagesConfig.personList.url,
            key: dynamicPagesConfig.personList.url,
            icon: TeamOutlined,
          },
          {
            title: "Add persons",
            link: dynamicPagesConfig.personUpload.url,
            key: dynamicPagesConfig.personUpload.url,
            icon: UsergroupAddOutlined,
          },
        ]
      },
      {
        label: "Manage STR data",
        menus: [
          {
            title: "Add ForenSeq",
            link: dynamicPagesConfig.excelUpload.url,
            key: dynamicPagesConfig.excelUpload.url,
            icon: FileAddOutlined,
          },
        ]
      },
      {
        label: "Explore",
        menus: [
          {
            title: "Pattern Alignment",
            link: dynamicPagesConfig.patternAlignment.url,
            key: dynamicPagesConfig.patternAlignment.url,
            icon: BarcodeOutlined,
          },
        ]
      },
      {
        label: "Export",
        menus: [
          {
            title: "Export pattern alignment as an excel file",
            link: dynamicPagesConfig.exportAsExcel.url,
            key: dynamicPagesConfig.exportAsExcel.url,
            icon: FileExcelOutlined,
          },
        ]
      },
    ],
  },
  {
    name: "Admin",
    key: "Admin",
    roles: [ADMIN_ROLE],
    link: null,
    icon: ReconciliationOutlined,
    notificationURL: "/notification/pattern-alignment-generation",
    notification: (data) => {
      if (data && data.count !== 0) {
        return {[dynamicPagesConfig.managePatternAlignment.url]: 1};
      }
      return null;
    },
    submenuGroup: [
      {
        label: "User Management",
        menus: [
          {
            title: "Manage current users",
            link: dynamicPagesConfig.manageAllUser.url,
            key: dynamicPagesConfig.manageAllUser.url,
            icon: UserSwitchOutlined,
          },
          {
            title: "Approve new users",
            link: dynamicPagesConfig.manageNewUser.url,
            key: dynamicPagesConfig.manageNewUser.url,
            icon: UsergroupAddOutlined,
          },
        ]
      },
      {
        label: "Analysis",
        menus: [
          {
            title: "Pattern Alignment",
            link: dynamicPagesConfig.managePatternAlignment.url,
            key: dynamicPagesConfig.managePatternAlignment.url,
            icon: ProjectOutlined,
          },
        ]
      },
      {
        label: "General Config",
        menus: [
          {
            title: "Kits and loci",
            link: dynamicPagesConfig.manageLocus.url,
            key: dynamicPagesConfig.manageLocus.url,
            icon: UnorderedListOutlined,
          },
          {
            title: "Map",
            link: dynamicPagesConfig.manageMap.url,
            key: dynamicPagesConfig.manageMap.url,
            icon: SlidersOutlined,
          },
          {
            title: "Country, Region, and District",
            link: dynamicPagesConfig.manageCountry.url,
            key: dynamicPagesConfig.manageCountry.url,
            icon: GlobalOutlined,
          },
          {
            title: "Race",
            link: dynamicPagesConfig.manageRaces.url,
            key: dynamicPagesConfig.manageRaces.url,
            icon: UserOutlined,
          },
        ]
      },
      {
        label: "Pattern Alignment Config",
        menus: [
          {
            title: "Referenced STR repeat motifs patterns",
            link: dynamicPagesConfig.manageUploadRefPattern.url,
            key: dynamicPagesConfig.manageUploadRefPattern.url,
            icon: CloudUploadOutlined,
          },
        ]
      },
      {
        label: "Profile Search Config",
        menus: [
          {
            title: "Core Loci",
            link: dynamicPagesConfig.manageCoreLoci.url,
            key: dynamicPagesConfig.manageCoreLoci.url,
            icon: PartitionOutlined,
          },
          {
            title: "Manage Length-Based Allele Frequencies",
            link: dynamicPagesConfig.manageStatsProfile.url,
            key: dynamicPagesConfig.manageStatsProfile.url,
            icon: IdcardOutlined,
          },
          {
            title: "Homozygous Genotype Calculation",
            link: dynamicPagesConfig.manageCalculationProfileSearch.url,
            key: dynamicPagesConfig.manageCalculationProfileSearch.url,
            icon: CalculatorOutlined,
          },
        ]
      }
    ],
  },
  {
    name: "My profile",
    key: "my-profile",
    roles: [LAB_USER_ROLE, ADMIN_ROLE],
    link: null,
    icon: UserOutlined,
    submenuGroup: [
      {
        label: "Manage profile",
        menus: [
          {
            title: "My profile",
            link: dynamicPagesConfig.myProfile.url,
            key: dynamicPagesConfig.myProfile.url,
            icon: ProfileOutlined,
          },
          {
            title: "Change password",
            link: dynamicPagesConfig.changePwd.url,
            key: dynamicPagesConfig.changePwd.url,
            icon: RetweetOutlined,
          },
        ]
      },
    ]
  }
]

export default menus;