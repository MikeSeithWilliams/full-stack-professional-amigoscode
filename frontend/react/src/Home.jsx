import {Text, Wrap} from '@chakra-ui/react'
import SidebarWithHeader from "./components/shared/SideBar.jsx";


const Home = () => {
    return (
      <SidebarWithHeader>
          <Wrap spacing='30px' justify='center'>
              <Text fontSize={"6xl"}>Dashboard</Text>
          </Wrap>
      </SidebarWithHeader>
    )
}

export default Home