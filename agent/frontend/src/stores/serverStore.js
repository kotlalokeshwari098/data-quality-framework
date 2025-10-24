import { reactive } from 'vue';
import { api } from '../js/api.js';

const state = reactive({
  servers: [],
  loading: false,
  error: null
});

const serverStore = {
  get servers() {
    return state.servers;
  },

  get loading() {
    return state.loading;
  },

  get error() {
    return state.error;
  },

  async fetchServers() {
    state.loading = true;
    state.error = null;

    try {
      const response = await api.get('/api/servers');

      // Handle HATEOAS response structure
      if (response.data._embedded && response.data._embedded.servers) {
        state.servers = response.data._embedded.servers.map(item => item.content || item);
      } else if (Array.isArray(response.data)) {
        state.servers = response.data;
      } else {
        state.servers = [];
      }

      return state.servers;
    } catch (error) {
      state.error = 'Failed to fetch servers';
      console.error('Error fetching servers:', error);
      throw error;
    } finally {
      state.loading = false;
    }
  },

  async createServer(serverData) {
    state.loading = true;
    state.error = null;

    try {
      const response = await api.post('/api/servers', {
        url: serverData.url,
        name: serverData.name
      });

      // Extract server data from HATEOAS response
      const newServer = response.data.content || response.data;
      state.servers.push(newServer);

      return newServer;
    } catch (error) {
      state.error = error.response?.data?.message || 'Failed to create server';
      console.error('Error creating server:', error);
      throw error;
    } finally {
      state.loading = false;
    }
  },

  async updateServer(id, updateData) {
    state.loading = true;
    state.error = null;

    try {
      const response = await api.put(`/api/servers/${id}`, {
        url: updateData.url,
        name: updateData.name
      });

      // Extract server data from HATEOAS response
      const updatedServer = response.data.content || response.data;

      // Update the server in the local state
      const index = state.servers.findIndex(server => server.id === id);
      if (index !== -1) {
        state.servers[index] = updatedServer;
      }

      return updatedServer;
    } catch (error) {
      state.error = error.response?.data?.message || 'Failed to update server';
      console.error('Error updating server:', error);
      throw error;
    } finally {
      state.loading = false;
    }
  },

  async deleteServer(id) {
    state.loading = true;
    state.error = null;

    try {
      await api.delete(`/api/servers/${id}`);

      // Remove the server from the local state
      const index = state.servers.findIndex(server => server.id === id);
      if (index !== -1) {
        state.servers.splice(index, 1);
      }

      return true;
    } catch (error) {
      state.error = error.response?.data?.message || 'Failed to delete server';
      console.error('Error deleting server:', error);
      throw error;
    } finally {
      state.loading = false;
    }
  },

  clearError() {
    state.error = null;
  }
};

export default serverStore;
