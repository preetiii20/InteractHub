# InteractHub - Complete Project Documentation

## 📋 Table of Contents
1. [Project Overview](#project-overview)
2. [System Architecture](#system-architecture)
3. [User Roles & Permissions](#user-roles--permissions)
4. [Microservices Breakdown](#microservices-breakdown)
5. [User Portals & Features](#user-portals--features)
6. [Complete Workflows](#complete-workflows)
7. [Technology Stack](#technology-stack)
8. [Database Schema](#database-schema)
9. [API Endpoints](#api-endpoints)
10. [Real-time Communication](#real-time-communication)

---

## 🎯 Project Overview

**InteractHub** is a comprehensive Enterprise Communication and Project Management Platform designed to facilitate seamless collaboration between administrators, HR personnel, managers, and employees. The system provides real-time communication, project management, task tracking, attendance management, leave requests, and global company communications.

### Key Features:
- **Multi-role User Management** (Admin, HR, Manager, Employee)
- **Real-time Chat & Communication** (WebSocket/STOMP)
- **Project & Task Management**
- **Group Chat for Project Teams**
- **File Upload & Sharing**
- **Video/Voice Calls** (WebRTC)
- **Attendance Tracking**
- **Leave Request Management**
- **Global Announcements & Polls**
- **Audit Logging & Monitoring**

---

## 🏗️ System Architecture

### Architecture Pattern: **Microservices**

The system follows a microservices architecture with separate backend services communicating via REST APIs and WebSockets.

```
┌─────────────┐
│   Frontend  │  React Application (Port 3000)
│   (React)   │
└──────┬──────┘
       │ HTTP REST + WebSocket
       │
       ├──────────────┬──────────────┬──────────────┬──────────────┐
       │              │              │              │              │
┌──────▼──────┐ ┌─────▼─────┐ ┌─────▼─────┐ ┌─────▼─────┐ ┌─────▼─────┐
│   Admin     │ │  Manager  │ │   Chat    │ │   Notify  │ │   HR      │
│  Service    │ │  Service  │ │  Service  │ │  Service  │ │  Service  │
│   :8081     │ │   :8083   │ │   :8085   │ │   :8090   │ │   :8087   │
└─────────────┘ └───────────┘ └───────────┘ └───────────┘ └───────────┘
       │              │              │              │              │
       └──────────────┴──────────────┴──────────────┴──────────────┘
                              │
                    ┌─────────▼─────────┐
                    │   H2 Database     │
                    │   (In-Memory)     │
                    └───────────────────┘
```

### Service Communication:
- **REST APIs** for synchronous communication
- **WebSocket/STOMP** for real-time messaging
- **Inter-service calls** via RestTemplate

---

## 👥 User Roles & Permissions

### 1. **ADMIN** (Administrator)
**Port:** 8081

**Responsibilities:**
- Complete system oversight and control
- User account management (create, update, deactivate)
- System-wide communications and announcements
- Audit log monitoring
- Live system monitoring
- Account creation for all user types

**Default Credentials:**
- Email: `admin@interacthub.com`
- Password: `admin123`

---

### 2. **HR** (Human Resources)
**Port:** 8087

**Responsibilities:**
- Employee management and onboarding
- Attendance monitoring and approval
- Leave request processing and approval
- Global company communications
- Employee data maintenance

**Key Features:**
- Employee profile management
- Attendance tracking dashboard
- Leave request workflow
- Company-wide announcements

---

### 3. **MANAGER**
**Port:** 8083

**Responsibilities:**
- Project creation and management
- Task assignment and tracking
- Employee onboarding for their team
- Project group management
- Team communication
- Employee performance oversight

**Key Features:**
- Project lifecycle management
- Task assignment to employees
- Project groups with group chat
- Employee management within projects
- Team announcements

---

### 4. **EMPLOYEE**
**Port:** 8086

**Responsibilities:**
- View assigned projects and tasks
- Submit attendance
- Request leave
- Participate in company communications
- Chat with team members
- Access company updates

**Key Features:**
- Project and task visibility
- Attendance submission
- Leave request submission
- Company updates and polls
- Real-time chat

---

## 🔧 Microservices Breakdown

### 1. **Admin Service** (Port 8081)
**Purpose:** Central authentication, authorization, and user management

**Key Components:**
- User authentication (JWT)
- User CRUD operations
- Role-based access control
- Department management
- Audit logging integration

**Endpoints:**
- `/api/admin/users` - User management
- `/api/admin/auth/*` - Authentication
- `/api/admin/departments` - Department management

**Database:** H2 in-memory (users table)

---

### 2. **Manager Service** (Port 8083)
**Purpose:** Project, task, and team management

**Key Components:**
- Project management
- Task assignment
- Project groups
- Employee onboarding
- Team communication integration

**Endpoints:**
- `/api/manager/projects` - Project CRUD
- `/api/manager/tasks` - Task management
- `/api/manager/project-groups` - Group management
- `/api/manager/employees` - Employee management

**Database:** H2 in-memory (projects, tasks, project_groups tables)

**Inter-service Communication:**
- Calls Admin Service to get employee emails
- Calls Chat Service to create group chats
- Calls Notification Service for alerts

---

### 3. **Chat Service** (Port 8085)
**Purpose:** Real-time messaging and communication

**Key Components:**
- WebSocket/STOMP messaging
- Group chat management
- Direct messaging
- File upload/download
- Video/voice call signaling

**Endpoints:**
- `/api/group/*` - Group chat operations
- `/api/direct/*` - Direct messaging
- `/api/group/upload-file` - File uploads
- `/api/group/files/{fileName}` - File downloads
- `/ws` - WebSocket endpoint

**WebSocket Destinations:**
- `/topic/group.{groupId}` - Group chat messages
- `/user/{email}/queue/notify` - Direct messages
- `/topic/channel.{channelId}.chat` - Channel messages

**Database:** H2 in-memory (chat_groups, group_members, group_messages tables)

---

### 4. **Notification Service** (Port 8090)
**Purpose:** Email and notification delivery

**Key Components:**
- Welcome email sending
- Password reset emails
- System notifications
- Integration with external email service

**Endpoints:**
- `/api/notify/welcome-user` - Welcome emails
- `/api/notify/reset-password` - Password reset

---

### 5. **HR Service** (Port 8087)
**Purpose:** Human resources operations

**Key Components:**
- Employee attendance tracking
- Leave request management
- Employee profile management
- HR dashboard analytics

**Endpoints:**
- `/api/hr/attendance` - Attendance operations
- `/api/hr/leave-requests` - Leave management
- `/api/hr/employees` - Employee management

---

### 6. **Employee Service** (Port 8086)
**Purpose:** Employee-specific operations

**Key Components:**
- Employee dashboard
- Attendance submission
- Leave request submission
- Project/task viewing

**Endpoints:**
- `/api/employee/attendance` - Attendance submission
- `/api/employee/leave` - Leave requests
- `/api/employee/projects` - Project viewing

---

## 🌐 User Portals & Features

### **ADMIN Portal** (`/dashboard/admin`)

#### **1. Dashboard** (`/dashboard/admin`)
- System overview statistics
- User count by role
- Recent activities
- System health monitoring

#### **2. User Management** (`/dashboard/admin/users`)
- View all users
- Create new user accounts
- Edit user details
- Activate/deactivate users
- Filter by role

#### **3. Global Communications** (`/dashboard/admin/comms`)
- Create company-wide announcements
- Create polls
- Broadcast messages
- View communication history

#### **4. Live Communication** (`/dashboard/admin/live`)
- Real-time chat hub
- Direct messaging to any user
- Group chat creation
- Video/voice call initiation

#### **5. Account Creation** (`/dashboard/admin/accounts`)
- Bulk account creation
- Role assignment
- Department assignment

#### **6. Monitoring** (`/dashboard/admin/monitoring`)
- System activity monitoring
- Service health checks
- User activity logs

#### **7. Audit Logs** (`/dashboard/admin/audit`)
- View all system actions
- Filter by user, date, action type
- Export audit data

---

### **MANAGER Portal** (`/dashboard/manager`)

#### **1. Dashboard** (`/dashboard/manager`)
- Overview of projects
- Task statistics
- Team member overview
- Recent activities

#### **2. Projects** (`/dashboard/manager/projects`)
- Create new projects
- View all projects
- Update project status (Planned, Active, Completed, Cancelled)
- View project details
- Create project groups within projects

#### **3. Project Groups** (`/dashboard/manager/groups`)
- Create project groups
- Assign employees to groups
- View group members
- Open group chat for each group
- Edit/delete groups

#### **4. Tasks** (`/dashboard/manager/tasks`)
- Create tasks
- Assign tasks to employees
- Set task priorities
- Update task status
- View task progress

#### **5. Employees** (`/dashboard/manager/employees`)
- View assigned employees
- View employee details
- Assign employees to projects

#### **6. Onboard Employee** (`/dashboard/manager/onboard`)
- Create new employee accounts
- Set initial credentials
- Assign to projects immediately

#### **7. Live Communication** (`/dashboard/manager/communication`)
- Direct messaging
- Group chats
- Video/voice calls

#### **8. Global Comms** (`/dashboard/manager/comms`)
- View company announcements
- View and vote on polls
- Company updates

---

### **EMPLOYEE Portal** (`/dashboard/employee`)

#### **1. Dashboard** (`/dashboard/employee`)
- Personal overview
- Assigned tasks
- Upcoming deadlines
- Recent activities

#### **2. Projects** (`/dashboard/employee/projects`)
- View assigned projects
- View project groups
- Open group chat for project groups
- View project details

#### **3. Attendance** (`/dashboard/employee/attendance`)
- Mark daily attendance
- View attendance history
- View attendance statistics

#### **4. Leave Requests** (`/dashboard/employee/leave-requests`)
- Submit leave requests
- View leave request status
- View leave history
- Leave balance

#### **5. Company Updates** (`/dashboard/employee/company-updates`)
- View company announcements
- View and vote on polls
- Company news feed

#### **6. Live Communication** (`/dashboard/employee/chat`)
- Direct messaging with colleagues
- Group chats
- Real-time notifications

---

### **HR Portal** (`/dashboard/hr`)

#### **1. Dashboard** (`/dashboard/hr`)
- HR overview statistics
- Pending leave requests
- Attendance summary
- Employee count

#### **2. Employee Management** (`/dashboard/hr/employees`)
- View all employees
- Edit employee profiles
- View employee details
- Department management

#### **3. Attendance** (`/dashboard/hr/attendance`)
- View all employee attendance
- Attendance reports
- Attendance approval/rejection

#### **4. Leave Requests** (`/dashboard/hr/leave-requests`)
- View all leave requests
- Approve/reject leave requests
- Leave balance management
- Leave reports

#### **5. Global Communications** (`/dashboard/hr/global-comms`)
- Create announcements
- Create polls
- Company-wide messaging

#### **6. Live Communication** (`/dashboard/hr/communication`)
- Direct messaging
- Group chats
- Employee communication

---

## 🔄 Complete Workflows

### **1. User Authentication Flow**

```
1. User visits /login
2. Enters email and password
3. Frontend sends POST to /api/admin/auth/login
4. Admin Service validates credentials
5. Admin Service generates JWT token
6. Token stored in localStorage (key: "interacthub_token")
7. User object stored in localStorage (key: "interacthub_user")
8. Redirect based on role:
   - ADMIN → /dashboard/admin
   - MANAGER → /dashboard/manager
   - HR → /dashboard/hr
   - EMPLOYEE → /dashboard/employee
```

### **2. Project Creation & Group Chat Flow (Manager)**

```
1. Manager logs in → Manager Dashboard
2. Navigates to Projects → Creates new project
3. Manager Service saves project → Creates project chat channel
4. Manager creates Project Group:
   - Selects project
   - Enters group name and description
   - Selects employees (checkboxes)
   - Submits form
5. Manager Service:
   - Saves ProjectGroup with employeeIds
   - Calls Chat Service to create group chat
   - Chat Service creates ChatGroup with all member emails
   - Manager Service links chatGroupId to ProjectGroup
6. Employees can now:
   - See project group in their portal
   - Click project → Opens group chat
   - All group members + manager are in chat
```

### **3. Task Assignment Flow**

```
1. Manager creates task in Task Management
2. Selects project and assigns to employee
3. Sets priority and due date
4. Manager Service saves task
5. Employee receives notification (via WebSocket)
6. Employee views task in their dashboard
7. Employee updates task status (In Progress, Completed)
8. Manager sees updated status in real-time
```

### **4. Attendance Submission Flow (Employee)**

```
1. Employee navigates to Attendance
2. Clicks "Mark Attendance" for today
3. Employee Service saves attendance record
4. Status: PENDING
5. HR views pending attendance
6. HR approves/rejects
7. Employee receives notification
8. Status updated: APPROVED or REJECTED
```

### **5. Leave Request Flow**

```
1. Employee navigates to Leave Requests
2. Fills leave request form:
   - Leave type (Sick, Vacation, Personal)
   - Start date, End date
   - Reason
3. Employee Service saves leave request
4. Status: PENDING
5. HR receives notification
6. HR views request in Leave Requests
7. HR approves/rejects with comments
8. Employee receives notification
9. Status updated: APPROVED or REJECTED
```

### **6. Group Chat Creation Flow**

```
1. Manager creates Project Group
2. Manager Service calls Chat Service:
   POST /api/group/create
   {
     "name": "Project Group Name",
     "createdByName": "manager@email.com",
     "members": ["employee1@email.com", "employee2@email.com", "manager@email.com"]
   }
3. Chat Service:
   - Creates ChatGroup entity
   - Creates GroupMember entries for each member
   - Returns chatGroupId
4. Manager Service saves chatGroupId to ProjectGroup
5. Frontend opens chat:
   - Connects to WebSocket (/ws)
   - Subscribes to /topic/group.{chatGroupId}
   - Loads chat history from /api/group/{chatGroupId}/history
```

### **7. File Upload in Chat Flow**

```
1. User selects file in chat window
2. Frontend creates FormData:
   - file: File object
   - groupId: Chat group ID
   - senderName: User email
   - content: Optional message
3. POST /api/group/upload-file
4. Chat Service:
   - FileStorageService stores file in chat-uploads/
   - Creates GroupMessage with file metadata
   - Saves message to database
   - Broadcasts message via WebSocket to /topic/group.{groupId}
5. All group members receive file message in real-time
6. Users can click file → Downloads via /api/group/files/{fileName}
```

### **8. Direct Messaging Flow**

```
1. User opens Live Communication
2. Selects recipient from dropdown
3. Types message and sends
4. Frontend sends via WebSocket:
   Destination: /app/direct.send
   Payload: { toName: "recipient@email.com", content: "message" }
5. Chat Service receives message
6. Saves DirectMessage to database
7. Forwards to recipient:
   Destination: /user/{recipientEmail}/queue/notify
8. Recipient receives notification
9. Both users can view message history
```

### **9. Company Announcement Flow (Admin/HR)**

```
1. Admin/HR creates announcement
2. Fills form:
   - Title
   - Content
   - Target audience (all, specific roles)
   - Optional: Attachment
3. Chat Service saves announcement
4. Broadcasts to all users or selected audience
5. All employees see announcement in Company Updates
6. Employees can interact (like, comment)
7. Interactions saved in Interaction entity
```

### **10. Poll Creation & Voting Flow**

```
1. Admin/HR creates poll
2. Sets question and options
3. Chat Service saves poll
4. Broadcasts to target audience
5. Employees see poll in Company Updates
6. Employee clicks option to vote
7. PollVote saved to database
8. Real-time results updated
9. Shows vote percentages
```

---

## 💻 Technology Stack

### **Frontend:**
- **React 18** - UI Framework
- **React Router** - Routing
- **Axios** - HTTP client
- **@stomp/stompjs** - STOMP WebSocket client
- **sockjs-client** - WebSocket fallback
- **Framer Motion** - Animations
- **Tailwind CSS** - Styling

### **Backend:**
- **Spring Boot 3.5.6** - Framework
- **Java 21** - Programming language
- **Spring Data JPA** - Database abstraction
- **H2 Database** - In-memory database (dev)
- **Spring Security** - Authentication & Authorization
- **JWT** - Token-based auth
- **WebSocket/STOMP** - Real-time messaging
- **RestTemplate** - Inter-service communication
- **Maven** - Build tool

### **Real-time Communication:**
- **WebSocket** - Bidirectional communication
- **STOMP** - Messaging protocol
- **SockJS** - WebSocket fallback

### **File Storage:**
- **Local File System** - chat-uploads/ directory
- **Multipart File Upload** - Spring Boot

---

## 🗄️ Database Schema

### **Admin Service Database:**
```sql
users (
  id BIGINT PRIMARY KEY,
  email VARCHAR(255) UNIQUE,
  password VARCHAR(255),
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  role VARCHAR(20), -- ADMIN, HR, MANAGER, EMPLOYEE
  department_id BIGINT,
  position VARCHAR(100),
  phone_number VARCHAR(50),
  is_active BOOLEAN,
  created_by BIGINT,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
)
```

### **Manager Service Database:**
```sql
projects (
  id BIGINT PRIMARY KEY,
  name VARCHAR(255),
  description TEXT,
  manager_id BIGINT,
  status VARCHAR(20), -- PLANNED, ACTIVE, COMPLETED, CANCELLED
  start_date DATE,
  end_date DATE,
  created_at TIMESTAMP
)

tasks (
  id BIGINT PRIMARY KEY,
  project_id BIGINT,
  employee_id BIGINT,
  title VARCHAR(255),
  description TEXT,
  status VARCHAR(20), -- PENDING, IN_PROGRESS, COMPLETED
  priority VARCHAR(20), -- LOW, MEDIUM, HIGH
  due_date DATE,
  created_at TIMESTAMP
)

project_groups (
  id BIGINT PRIMARY KEY,
  project_id BIGINT,
  name VARCHAR(255),
  description TEXT,
  chat_group_id VARCHAR(255),
  created_at TIMESTAMP
)

project_groups_employee_ids (
  project_group_id BIGINT,
  employee_id BIGINT
)
```

### **Chat Service Database:**
```sql
chat_groups (
  group_id VARCHAR(255) PRIMARY KEY,
  name VARCHAR(255),
  created_by_name VARCHAR(255),
  created_at TIMESTAMP
)

group_members (
  id BIGINT PRIMARY KEY,
  group_id VARCHAR(255),
  member_name VARCHAR(255)
)

group_messages (
  id BIGINT PRIMARY KEY,
  group_id VARCHAR(255),
  sender_name VARCHAR(255),
  content TEXT,
  file_url VARCHAR(255),
  file_name VARCHAR(255),
  file_type VARCHAR(100),
  file_size BIGINT,
  sent_at TIMESTAMP
)
```

---

## 🔌 API Endpoints

### **Admin Service (8081)**
```
POST   /api/admin/auth/login
POST   /api/admin/auth/logout
GET    /api/admin/users
POST   /api/admin/users
PUT    /api/admin/users/{id}
DELETE /api/admin/users/{id}
GET    /api/admin/users/{id}
```

### **Manager Service (8083)**
```
GET    /api/manager/projects/my/{managerId}
POST   /api/manager/projects
PUT    /api/manager/projects/{id}
DELETE /api/manager/projects/{id}
GET    /api/manager/project-groups/{managerId}
POST   /api/manager/project-groups
GET    /api/manager/project-groups/{groupId}/chat-id
PUT    /api/manager/project-groups/{groupId}/add-employee/{employeeId}
DELETE /api/manager/project-groups/{groupId}/remove-employee/{employeeId}
GET    /api/manager/tasks
POST   /api/manager/tasks
PUT    /api/manager/tasks/{id}
GET    /api/manager/employees/{managerId}
```

### **Chat Service (8085)**
```
POST   /api/group/create
GET    /api/group/{groupId}/members
GET    /api/group/{groupId}/history
POST   /api/group/upload-file
GET    /api/group/files/{fileName}
POST   /api/direct/send
GET    /api/direct/history
WebSocket: /ws
```

### **Notification Service (8090)**
```
POST   /api/notify/welcome-user
POST   /api/notify/reset-password
```

---

## 📡 Real-time Communication

### **WebSocket Connection:**
- **Endpoint:** `http://localhost:8085/ws`
- **Protocol:** STOMP over WebSocket
- **Library:** @stomp/stompjs with SockJS fallback

### **Message Destinations:**

#### **Group Chat:**
- **Subscribe:** `/topic/group.{groupId}`
- **Send:** `/app/group.send`
- **Payload:** `{ groupId, senderName, content, fileUrl?, fileName?, fileType? }`

#### **Direct Messages:**
- **Subscribe:** `/user/{email}/queue/notify`
- **Send:** `/app/direct.send`
- **Payload:** `{ toName, fromName, content, type: "dm" }`

#### **Channel Messages:**
- **Subscribe:** `/topic/channel.{channelId}.chat`
- **Send:** `/app/chat.sendMessage`
- **Payload:** `{ channelId, senderName, content }`

### **Video/Voice Calls:**
- **WebRTC Signaling:** `/app/signal.*`
- **Offer:** `/app/signal.offer`
- **Answer:** `/app/signal.answer`
- **ICE Candidate:** `/app/signal.candidate`
- **Hangup:** `/app/signal.hangup`

---

## 🚀 Getting Started

### **Prerequisites:**
- Java 21
- Node.js 16+
- Maven 3.6+
- H2 Database (or MySQL for production)

### **Backend Setup:**
```bash
# Start each microservice
cd backend-microservices/admin-service && mvn spring-boot:run
cd backend-microservices/manager && mvn spring-boot:run
cd backend-microservices/chat && mvn spring-boot:run
cd backend-microservices/notify && mvn spring-boot:run
cd backend-microservices/hr-service && mvn spring-boot:run
cd backend-microservices/employee && mvn spring-boot:run
```

### **Frontend Setup:**
```bash
cd frontend
npm install
npm start
```

### **Default Login:**
- **Admin:** admin@interacthub.com / admin123

---

## 📝 Key Features Summary

### **For Managers:**
✅ Create and manage projects  
✅ Create project groups with team chat  
✅ Assign tasks to employees  
✅ Onboard new employees  
✅ Real-time team communication  
✅ View project progress  

### **For Employees:**
✅ View assigned projects and tasks  
✅ Participate in project group chats  
✅ Submit attendance  
✅ Request leave  
✅ View company updates  
✅ Vote on polls  

### **For HR:**
✅ Manage employee profiles  
✅ Approve/reject attendance  
✅ Process leave requests  
✅ Create company announcements  
✅ Generate HR reports  

### **For Admins:**
✅ Complete system control  
✅ User account management  
✅ System monitoring  
✅ Audit log viewing  
✅ Global communications  

---

## 🔒 Security Features

- **JWT Authentication** - Token-based auth
- **Role-Based Access Control (RBAC)** - Permission system
- **Password Hashing** - Secure password storage
- **CORS Configuration** - Cross-origin protection
- **Session Management** - Secure session handling

---

## 📊 Monitoring & Logging

- **Audit Logs** - All user actions tracked
- **System Monitoring** - Service health checks
- **Activity Tracking** - User activity logs
- **Error Logging** - Comprehensive error tracking

---

## 🎯 Future Enhancements

- [ ] Email notifications for important events
- [ ] Mobile application
- [ ] Advanced analytics dashboard
- [ ] Integration with external calendar systems
- [ ] Document versioning
- [ ] Advanced reporting features
- [ ] Multi-language support

---

**Last Updated:** October 2025  
**Version:** 1.0.0  
**Status:** Production Ready

