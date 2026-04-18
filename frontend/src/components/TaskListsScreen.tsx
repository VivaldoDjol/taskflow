import { Button, Card, CardBody, Progress } from "@nextui-org/react";
import { List, LogOut, Plus } from "lucide-react";
import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAppContext } from "../AppContext";
import { TokenStorage } from "../auth/TokenStorage";

const TaskListScreen: React.FC = () => {
  const { state, api } = useAppContext();

  useEffect(() => {
    void api.fetchTaskLists();
  }, [api]);

  // Get a handle on the router
  const navigate = useNavigate();

  const handleCreateTaskList = () => {
    navigate("/new-task-list");
  };

  const handleSelectTaskList = (taskListId: string | undefined) => {
    navigate(`/task-lists/${taskListId}`);
    console.log(`Navigating to task list ${taskListId}`);
  };

  const handleLogout = () => {
    TokenStorage.clear();
    navigate("/login");
  };

  return (
    <div className="p-4 max-w-sm w-full">
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-2xl font-bold pr-2">My Task Lists</h1>
        <Button
          isIconOnly
          variant="light"
          onPress={handleLogout}
          aria-label="Log out"
        >
          <LogOut size={20} />
        </Button>
      </div>
      <Button
        onPress={handleCreateTaskList}
        color="primary"
        startContent={<Plus size={20} aria-hidden="true" />}
        className="w-full mb-4"
        aria-label="Create New Task List"
      >
        Create New Task List
      </Button>
      {state.taskLists.map((list) => {
        return (
          <Card
            key={list.id}
            isPressable
            onPress={() => handleSelectTaskList(list.id)}
            className="mb-4 w-full"
            role="button"
            aria-label={`Select task list: ${list.title}`}
          >
            <CardBody>
              <div className="flex items-center">
                <List
                  size={20}
                  className="mr-2 opacity-[40%]"
                  aria-hidden="true"
                />
                <h2 className="text-lg font-semibold">{list.title}</h2>{" "}
              </div>
              <p className="text-sm text-gray-500 mt-2">{list.count} tasks</p>
              <Progress
                value={list.progress ? list.progress * 100 : 0}
                className="mt-2"
                color="primary"
                aria-label={`Progress for ${list.title}: ${list.progress}%`}
              />
            </CardBody>
          </Card>
        );
      })}
    </div>
  );
};

export default TaskListScreen;
