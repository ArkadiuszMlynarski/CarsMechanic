package com.msi.CarsMechanic.CarsMechanic.Issue.Service;

import com.msi.CarsMechanic.CarsMechanic.Issue.Entity.Backlog;
import com.msi.CarsMechanic.CarsMechanic.Issue.Entity.Issue;
import com.msi.CarsMechanic.CarsMechanic.Issue.Repository.BacklogRepository;
import com.msi.CarsMechanic.CarsMechanic.Issue.Repository.IssueRepository;
import com.msi.CarsMechanic.CarsMechanic.Issue.exceptions.IssueIdException;
import com.msi.CarsMechanic.CarsMechanic.Issue.exceptions.IssueNotFoundException;
import com.msi.CarsMechanic.CarsMechanic.User.Entity.User;
import com.msi.CarsMechanic.CarsMechanic.User.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IssueService {

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private UserRepository userRepository;

    public Issue saveOrUpdateIssue(Issue issue, String username){

        if(issue.getIssueId() != null){
            Issue existingIssue = issueRepository.findByIssueId(issue.getIssueId());

            if(existingIssue != null && (!existingIssue.getIssueLeader().equals(username))){
                throw new IssueNotFoundException("Issue not found in your account");
            } else if (existingIssue == null){
                throw new IssueNotFoundException("Issue with ID:'"+issue.getIssueId()+"' cannot be updated, because it doesnt exist");
            }
        }

        User user = userRepository.findByUsername(username);

        issue.setUser(user);
        issue.setIssueLeader(user.getUsername());

        //creating new issue
        if(issue.getIssueId()==null){
            Backlog backlog = new Backlog();
            issue.setBacklog(backlog);
            backlog.setIssue(issue);
            backlog.setIssue_id(issue.getIssueId());
        }

        //updating existing issue
        if(issue.getIssueId()!=null){
            issue.setBacklog(backlogRepository.findByTaskid(issue.getIssueId()));
        }

        return issueRepository.save(issue);
    }

    public void deleteIssueById(Long id, String username){
        issueRepository.delete(findByIssueId(id, username));
    }

    public Iterable<Issue> findAllIssues(String username) {
        return issueRepository.findAllByIssueLeader(username);
    }


    public Issue findByIssueId(Long id, String username) {

        Issue issue = issueRepository.findByIssueId(id);

        if(issue == null){
            throw new IssueIdException("Issue with ID '"+id+"' not found");
        }

        if(!issue.getIssueLeader().equals(username)){
            throw new IssueNotFoundException("Issue not found in your account");
        }


        return issue;
    }
}