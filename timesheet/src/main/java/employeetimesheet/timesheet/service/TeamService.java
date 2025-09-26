package employeetimesheet.timesheet.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import employeetimesheet.timesheet.entity.Teams;
import employeetimesheet.timesheet.repository.TeamRepository;

@Service
public class TeamService {
    
@Autowired
TeamRepository teamRepository;

    public List <Teams> getallteams(){
        return teamRepository.findAll();
       }


       public Teams getteamperson(Long id){
        return teamRepository.findById(id).orElseThrow( () -> new IllegalArgumentException("user not found"+id));
       }

     public Teams postteams(Teams teams)  {
        return teamRepository.save(teams);
     }

    public Teams updateTeams(Long id,Teams teams){
         teams.setTeamname(teams.getTeamname());
         return teamRepository.save(teams);
    }


    public void  deleteteams(Long id){
        teamRepository.deleteById(id);
    }


    //to search with team by name like keyword
    public List <Teams> searchwithteamkeyword (String keyword){
        return teamRepository.findByTeamnameContainingIgnoreCase(keyword);
        // .findbyteamekeyword(keyword);
        //findByTeameKeyword
    }


    //to search with team name
    public List <Teams> searchwithteamname(String teamname){
        return teamRepository.findByTeamname(teamname);
    }
}
