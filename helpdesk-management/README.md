# Helpdesk-registry / helpdesk management

## Introduction

Establishing proper communication with the Re3gistry community is a key asset for operating, maintaining and updating the Re3gistry software. The [helpdesk of the Re3gistry](https://github.com/ec-jrc/re3gistry/issues) is the core of the communication strategy since it is the platform where users can report bugs, propose new features and start discussions on the Re3gistry software. This document aims to illustrate the systematic workflow adopted by the Re3gistry team to organize, address and manage the issues reported by users in the Re3gistry helpdesk.

## Helpdesk management workflow

The helpdesk management workflow defines the actions performed by the Re3gistry team to address and solve the problems reported by the users of the Re3gistry software. The workflow uses several GitHub artefacts: labels, milestones, status and the project boards.

A set of [issue forms](<https://github.com/ec-jrc/re3gistry/tree/master/.github/ISSUE_TEMPLATE>) has been created to help the user select the appropriate type of template for its issue. These forms will help the user to fill the issue with all the information required properly. A label or an assignation to a specific user will also be applied automatically, depending on the configuration of each form.

### GitHub labels

To know the status of each issue reported in the helpdesk (from the initial assessment to the final implementation of a solution for it), several labels are used. These are listed on [this page](https://github.com/ec-jrc/re3gistry/issues/labels) and are described in more detail below in the chronological order in which they are used while managing each Re3gistry issue:

- **_question_**: In case the issue requires further information from the user, the Re3gistry Team asks the user to provide it in the issue.
- **_under analysis_**: This label is assigned after the issue has been opened and indicates that the Re3gistry Team is performing a first analysis to figure out what the problem is and how to address it; in case this requires further information from the user, the Re3gistry Team asks the user to provide it in the issue discussion.
- **_type:bug_**: This label is assigned when something isn’t working. The main purpose of this label is to obtain info about the typology of the issues and include this in reports.
- **_type:enhancement_**: This label is assigned when there is a new feature or request.
- **_under development_**: If the initial analysis reveals that a change in the Re3gistry software is needed, this label is assigned to the issue to indicate that the Re3gistry team is developing a solution for the reported problem.
- **_ready for testing_**: The solution is ready to test.
- **_solved_**: This label is assigned after the issue is internally tested and confirmed that it works.

The diagram below shows the full helpdesk management cycle for each issue, from the initial stage when it is opened to the final stage when it is closed.  It also includes and identifies the actions of the Re3gistry team.

```mermaid
%%{init: {"themeVariables": {"fontSize": "14px" }}}%%

flowchart TD

%% NEW ISSUE [NODE]
newIssue("New issue")

%% SET LABEL UNDER ANALYSIS [NODE]
labelUnderAnalysis("Set label 'under analysis'")
style labelUnderAnalysis stroke-width:4px,stroke:#006b75

%% ANALYZE ISSUE [NODE]
analyzeIssue("Analyze issue")

%% MORE INFORMATION IS NEEDED [RHOMBUS]
rhombusMoreInfo{"More \n information \n is needed"}

%% SET LABEL QUESTION [NODE]
labelQuestion("Set label 'question'")
style labelQuestion stroke-width:4px,stroke:#d876e3

%% PROVIDE INFORMATION [NODE]
provideInformation("Provide information")

%% IS AN ISSUE IN SOFTWARE ... [RHOMBUS]
rhombusIssueSoftware{"Is an issue in \n Software \n Registry \n Helpdesk?"}

%% THE ISSUE IS RELATED TO OTHER HELPDESK? [RHOMBUS]
rhombusRelatedHelpdesk{"The issue is \n related to other \n helpdesk?"}

%% TRANSFER THE ISSUE TO THE CORRECT REPOSITORY [NODE]
correctRepository("Transfer the issue to the correct repository")

%% GIVE FEEDBACK TO THE USER [NODE]
feedbackUser("Give feedback to the user")

%% CLASSIFY THE ISSUE ... [NODE]
classifyIssue("Classify the issue - Set label 'type:bug' / 'type:enhancement'")
style classifyIssue stroke-width:4px,stroke:#ed7d31

%% SET LABEL UNDER DEVELOPMENT [NODE]
labelUnderDev("Set label 'under development'")
style labelUnderDev stroke-width:4px,stroke:#8746b5

%% ASSIGN DEVELOPER [NODE]
assignDev("Assign developer")

%% DEVELOP [NODE]
develop("Develop")

%% SET LABEL READY FOR TESTING VALIDATE INTERNALLY [NODE]
labelTesting("Set label 'ready for testing' \n validate internally")
style labelTesting stroke-width:4px,stroke:#a5d1f7

%% SOLUTION IS VALID? [RHOMBUS]
rhombusValidSol{"Solution \n is valid?"}

%% SET LABEL SOLVED [NODE]
labelSolved("Set label 'solved'")
style labelSolved stroke-width:4px,stroke:#8ef984

%% ASSIGN MILESTONE [NODE]
assignMilestone("Assign milestone")

%% DEPLOY RELEASE [NODE]
deployRelease("Deploy release")

%% CLOSE THE ISSUE [NODE]
closeIssue("Close the issue")

%% RELATIONS

newIssue --> labelUnderAnalysis --> analyzeIssue
analyzeIssue --> rhombusMoreInfo
rhombusMoreInfo -- YES --> labelQuestion --> provideInformation
rhombusMoreInfo -- NO ---> rhombusIssueSoftware

provideInformation --> analyzeIssue

rhombusIssueSoftware -- YES --> classifyIssue
rhombusIssueSoftware -- NO --> rhombusRelatedHelpdesk

classifyIssue -->labelUnderDev --> assignDev --> develop 
develop --> labelTesting --> rhombusValidSol

rhombusValidSol -- YES --> labelSolved
rhombusValidSol -- NO --> labelUnderDev

labelSolved --> assignMilestone --> deployRelease ---> closeIssue

rhombusRelatedHelpdesk -- YES --> correctRepository
rhombusRelatedHelpdesk -- NO --> feedbackUser

feedbackUser ---> closeIssue


```


