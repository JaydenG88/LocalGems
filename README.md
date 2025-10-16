# LocalGems (On Pause)

LocalGems aims to be a platform where users can easily share and discover local and niche shops in their area or while traveling on vacation.

> **Status:** Work in Progress – Backend under active development  

## MVP Features (Planned / In Progress)
- User registration & login (Spring Security)
- User ability to submit a local business
  - Moderated using OpenAI's API to ensure its a business and local/niche
- Business search functionality
  - Search by category, city, ratings, and name
  - Google Maps API for a search map
- Users leaving reviews and ratings for businesses
- Users being able to save businesses  

## Possible Future Features
- Reddit-style forum page for each city and category
- Admin panel for verified business owners
  - analytics dashboard for their page
  
## Tech Stack
- **Backend:** Java, Spring Boot  
- **Database:** PostgreSQL  
- **APIs:** OpenAI API, Google Maps API  
- **Deployment:** AWS Elastic Beanstalk, Docker  

## Installation
Clone the repository:  
```bash
git clone <your-repo-link>
cd localgems
